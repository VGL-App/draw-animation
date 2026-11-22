package com.vgl.draw.animation.core.lessons.content

import com.vgl.draw.animation.core.lessons.samplePackEntries
import com.vgl.draw.animation.core.lessons.zipOf
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** A pack comes from the network: extracting it must never write outside its folder (plan T-048, section 3.1). */
class PackExtractorTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val root: File by lazy { tempFolder.newFolder("packs") }
    private val target: File get() = File(root, "v1/bouncing-ball")

    private fun zip(entries: Map<String, ByteArray>, method: Int = ZipEntry.DEFLATED): File =
        zipOf(File(tempFolder.newFolder(), "lesson.zip"), entries, method)

    private fun leftoverTempDirs(): List<String> =
        root.listFiles().orEmpty().map { it.name }.filter { it.startsWith(".tmp-") }

    private suspend fun assertRejected(extractor: PackExtractor, zip: File, case: String) {
        val error = runCatching { extractor.extract(zip, "v1", "bouncing-ball") }.exceptionOrNull()
        assertTrue("$case: $error", error is InvalidPackException)
        assertFalse(case, target.exists())
        assertEquals(case, emptyList<String>(), leftoverTempDirs())
    }

    @Test
    fun extract_samplePack_writesAllFiles() = runTest {
        val entries = LinkedHashMap<String, ByteArray>()
        entries["guides/"] = ByteArray(0)
        entries.putAll(samplePackEntries())
        val dir = PackExtractor(root).extract(zip(entries), "v1", "bouncing-ball")

        assertEquals(target, dir)
        for ((name, bytes) in samplePackEntries()) {
            assertArrayEquals(name, bytes, File(dir, name).readBytes())
        }
        assertTrue(File(dir, "guides").isDirectory)
        assertEquals(emptyList<String>(), leftoverTempDirs())
    }

    @Test
    fun extract_existingTarget_returnsWithoutRewriting() = runTest {
        val extractor = PackExtractor(root)
        val zip = zip(samplePackEntries())
        val first = extractor.extract(zip, "v1", "bouncing-ball")
        File(first, "guides/0.png").writeBytes(byteArrayOf(99))

        val second = extractor.extract(zip, "v1", "bouncing-ball")

        assertEquals(first, second)
        assertArrayEquals(byteArrayOf(99), File(second, "guides/0.png").readBytes())
    }

    @Test
    fun extract_ignoresMacMetadata() = runTest {
        val entries = LinkedHashMap(samplePackEntries())
        entries["__MACOSX/guides/._0.png"] = byteArrayOf(1)
        entries[".DS_Store"] = byteArrayOf(2)
        entries["guides/.DS_Store"] = byteArrayOf(3)
        val dir = PackExtractor(root).extract(zip(entries), "v1", "bouncing-ball")

        assertFalse(File(dir, "__MACOSX").exists())
        assertFalse(File(dir, ".DS_Store").exists())
        assertFalse(File(dir, "guides/.DS_Store").exists())
        for ((name, bytes) in samplePackEntries()) {
            assertArrayEquals(name, bytes, File(dir, name).readBytes())
        }

        // Control: an upper-case name is not Finder metadata, it is rejected.
        File(root, "v1").deleteRecursively()
        val upper = LinkedHashMap(samplePackEntries())
        upper["Guides/0.png"] = byteArrayOf(4)
        assertRejected(PackExtractor(root), zip(upper), "Guides/0.png")
    }

    @Test
    fun extract_unsafeName_throwsAndWritesNothing() = runTest {
        val names =
            listOf("../evil.txt", "a/../../evil.txt", "/abs.txt", "a\\b.png", "Guides/0.png", ".hidden/../../evil.txt")
        for (name in names) {
            val entries = linkedMapOf(
                "lesson.json" to samplePackEntries().getValue("lesson.json"),
                name to "evil".toByteArray(),
            )
            assertRejected(PackExtractor(root), zip(entries), name)
            assertTrue(name, tempFolder.root.walk().none { it.name == "evil.txt" || it.name == "abs.txt" })
        }
    }

    @Test
    fun extract_limits_throw() = runTest {
        val lessonJson = samplePackEntries().getValue("lesson.json")

        val fourEntries = linkedMapOf(
            "lesson.json" to lessonJson,
            "guides/0.png" to byteArrayOf(1),
            "guides/1.png" to byteArrayOf(2),
            "demo/0/0.png" to byteArrayOf(3),
        )
        assertRejected(PackExtractor(root, maxEntries = 3), zip(fourEntries), "4 entries")

        val big = zip(linkedMapOf("lesson.json" to ByteArray(101) { 'x'.code.toByte() }))
        assertRejected(PackExtractor(root, maxBytes = 100), big, "101 bytes")

        val bigIgnored = zip(linkedMapOf("lesson.json" to ByteArray(0), "__MACOSX/x" to ByteArray(101)))
        assertRejected(PackExtractor(root, maxBytes = 100), bigIgnored, "101 ignored bytes")

        val ignoredEntries = linkedMapOf(
            "lesson.json" to lessonJson,
            "__MACOSX/a" to byteArrayOf(1),
            "__MACOSX/b" to byteArrayOf(2),
            "__MACOSX/c" to byteArrayOf(3),
        )
        assertRejected(PackExtractor(root, maxEntries = 3), zip(ignoredEntries), "4 entries, 3 ignored")

        // ZipOutputStream refuses a duplicate name: write "lesson.jsoo" stored, then rename it in the bytes.
        val twin = zip(linkedMapOf("lesson.json" to lessonJson, "lesson.jsoo" to lessonJson), ZipEntry.STORED)
        twin.writeBytes(replaceAll(twin.readBytes(), "lesson.jsoo".toByteArray(), "lesson.json".toByteArray()))
        assertRejected(PackExtractor(root), twin, "duplicate lesson.json")

        // Control: the same 101 bytes pass with a limit of 101.
        assertEquals(target, PackExtractor(root, maxBytes = 101).extract(big, "v1", "bouncing-ball"))
    }

    @Test
    fun extract_notAZipOrNoLessonJson_isRejected() = runTest {
        val hello = File(tempFolder.newFolder(), "lesson.zip").apply { writeText("hello") }
        assertRejected(PackExtractor(root), hello, "not a zip")

        assertRejected(PackExtractor(root), zip(linkedMapOf("guides/0.png" to byteArrayOf(1))), "no lesson.json")

        val sample = zip(samplePackEntries()).readBytes()
        val truncated = File(tempFolder.newFolder(), "lesson.zip").apply { writeBytes(sample.copyOf(sample.size / 2)) }
        assertRejected(PackExtractor(root), truncated, "truncated zip")
    }

    @Test
    fun extract_concurrentSameLesson_extractsOnce() = runTest {
        val executor = Executors.newFixedThreadPool(4)
        val dispatcher = executor.asCoroutineDispatcher()
        try {
            val count = AtomicInteger()
            val extractor = PackExtractor(root, onExtract = { count.incrementAndGet() })
            val zip = zip(samplePackEntries())

            val dirs = List(8) { async(dispatcher) { extractor.extract(zip, "v1", "bouncing-ball") } }.awaitAll()

            assertEquals(List(8) { target }, dirs)
            assertEquals(listOf("v1"), root.listFiles().orEmpty().map { it.name })
            assertEquals(1, count.get())
        } finally {
            dispatcher.close()
        }
    }

    private fun replaceAll(bytes: ByteArray, from: ByteArray, to: ByteArray): ByteArray {
        require(from.size == to.size)
        val out = bytes.copyOf()
        var i = 0
        var replaced = 0
        while (i <= out.size - from.size) {
            if ((from.indices).all { out[i + it] == from[it] }) {
                to.copyInto(out, i)
                replaced++
                i += from.size
            } else {
                i++
            }
        }
        check(replaced > 0) { "Nothing replaced" }
        return out
    }
}
