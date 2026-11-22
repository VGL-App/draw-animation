package com.vgl.draw.animation.core.lessons.content

import com.vgl.draw.animation.core.lessons.isContentVersion
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.RandomAccessFile
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import kotlin.math.min
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** The pack is not a safe, complete lesson zip. Not an [IOException]: retrying never helps. */
internal class InvalidPackException(reason: String) : Exception(reason)

/**
 * Extracts `lesson.zip` into `root/<version>/<lessonId>/`, safely (plan T-048, section 3.1): entry names are checked
 * so nothing is written outside the folder, and the number of entries and of bytes read are limited. The pack is
 * extracted to a temporary folder renamed when complete, so the target folder exists only when whole.
 * [onExtract] is a test hook called each time a pack is really extracted.
 */
internal class PackExtractor(
    private val root: File,
    private val maxEntries: Int = 4_000,
    private val maxBytes: Long = 32L * 1024 * 1024,
    private val onExtract: () -> Unit = {},
) {
    private val locks = ConcurrentHashMap<String, Mutex>()

    /** Call it within an I/O dispatcher. Throws [InvalidPackException], or IOException for a disk error. */
    suspend fun extract(zip: File, version: String, lessonId: String): File {
        require(isContentVersion(version)) { "Invalid content version" }
        require(isContentId(lessonId)) { "Invalid lesson id" }
        val key = "$version/$lessonId"
        val target = File(root, key)
        if (target.isDirectory) return target
        // One extraction per lesson at a time; whoever waited finds the folder already there.
        return locks.getOrPut(key) { Mutex() }.withLock {
            if (target.isDirectory) return@withLock target
            onExtract()
            val temp = File(root, "$TEMP_PREFIX${UUID.randomUUID()}")
            try {
                if (!temp.mkdirs()) throw IOException("Could not create a temporary folder")
                unzip(zip, temp)
                target.parentFile?.mkdirs()
                if (!temp.renameTo(target) && !target.isDirectory) throw IOException("Could not move the pack in place")
                target
            } finally {
                if (temp.exists()) temp.deleteRecursively()
            }
        }
    }

    private fun unzip(zip: File, into: File) {
        // ZipInputStream reads entry by entry and stops quietly where a cut file ends: check the end record first.
        if (!hasEndRecord(zip)) throw InvalidPackException("not a zip or truncated zip")
        val base = into.canonicalFile.toPath()
        val names = HashSet<String>()
        var entries = 0
        var hasLessonJson = false
        val counter = ByteCounter(maxBytes)
        try {
            ZipInputStream(zip.inputStream().buffered()).use { input ->
                while (true) {
                    val entry = input.nextEntry ?: break
                    entries++
                    if (entries > maxEntries) throw InvalidPackException("more than $maxEntries entries")
                    val rawName = entry.name
                    if (isIgnored(rawName)) {
                        counter.copy(input, null)
                        continue
                    }
                    val name = if (entry.isDirectory) rawName.removeSuffix("/") else rawName
                    if (!ENTRY_NAME.matches(name)) throw InvalidPackException("unsafe entry name")
                    if (!names.add(name)) throw InvalidPackException("duplicate entry name")
                    val out = File(into, name)
                    if (!out.canonicalFile.toPath().startsWith(
                            base,
                        )
                    ) {
                        throw InvalidPackException("entry outside the pack")
                    }
                    if (entry.isDirectory) {
                        out.mkdirs()
                    } else {
                        out.parentFile?.mkdirs()
                        out.outputStream().buffered().use { counter.copy(input, it) }
                        if (name == LESSON_JSON) hasLessonJson = true
                    }
                }
            }
        } catch (e: ZipException) {
            // A damaged zip, or a name refused by the system's ZipPathValidator (Android 14+, targetSdk 34+).
            throw InvalidPackException("damaged zip")
        } catch (e: EOFException) {
            throw InvalidPackException("truncated zip")
        }
        if (!hasLessonJson) throw InvalidPackException("no $LESSON_JSON")
    }

    /** True when the end of central directory record is in the last bytes of [zip], as in every complete zip. */
    private fun hasEndRecord(zip: File): Boolean {
        val length = zip.length()
        if (length < END_RECORD_SIZE) return false
        val tailSize = min(length, (END_RECORD_SIZE + MAX_COMMENT_SIZE).toLong()).toInt()
        val tail = ByteArray(tailSize)
        RandomAccessFile(zip, "r").use {
            it.seek(length - tailSize)
            it.readFully(tail)
        }
        return (tailSize - END_RECORD_SIZE downTo 0).any { i ->
            END_RECORD_SIGNATURE.indices.all { tail[i + it] == END_RECORD_SIGNATURE[it] }
        }
    }

    /** Counts every byte read from the zip, kept or skipped, and stops past [limit]: the sizes in a zip may lie. */
    private class ByteCounter(private val limit: Long) {
        private var total = 0L
        private val buffer = ByteArray(BUFFER_SIZE)

        fun copy(input: InputStream, output: OutputStream?) {
            while (true) {
                val read = input.read(buffer)
                if (read < 0) return
                total += read
                if (total > limit) throw InvalidPackException("more than $limit bytes")
                output?.write(buffer, 0, read)
            }
        }
    }

    private companion object {
        const val TEMP_PREFIX = ".tmp-"
        const val LESSON_JSON = "lesson.json"
        const val BUFFER_SIZE = 8 * 1024
        const val END_RECORD_SIZE = 22
        const val MAX_COMMENT_SIZE = 0xFFFF
        val END_RECORD_SIGNATURE = byteArrayOf(0x50, 0x4b, 0x05, 0x06)

        /** Lower-case segments starting with a letter or digit: no "..", no absolute path, no backslash. */
        val ENTRY_NAME = Regex("[a-z0-9][a-z0-9._-]*(/[a-z0-9][a-z0-9._-]*)*")

        /**
         * Finder metadata (`__MACOSX/...`, `.DS_Store`, any segment starting with a dot) is skipped, unless the name
         * holds "..", a leading "/" or a backslash: such a name is always rejected.
         */
        fun isIgnored(name: String): Boolean {
            if (name.startsWith("/") || name.contains('\\')) return false
            val segments = name.split('/')
            if (segments.any { it == ".." }) return false
            return name.startsWith("__MACOSX/") || segments.any { it.startsWith(".") }
        }
    }
}
