package com.vgl.draw.animation.core.project.backup

import android.net.Uri
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectImport
import com.vgl.draw.animation.core.project.storage.ProjectImportCommitException
import com.vgl.draw.animation.core.project.storage.ProjectStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** One ZIP entry as a test sees it: its name and its uncompressed bytes. */
typealias Entry = Pair<String, ByteArray>

/**
 * [BackupStreams] on files: a `file://` [Uri] maps to its path. Records every mode [openWrite] received. A test sets
 * [readError] or [writeError] to make opening fail, and [wrapRead] or [wrapWrite] to wrap the opened stream.
 */
internal class FileBackupStreams : BackupStreams {
    val writeModes = mutableListOf<String>()
    var readError: Exception? = null
    var writeError: ((String) -> Exception?)? = null
    var wrapRead: (InputStream) -> InputStream = { it }
    var wrapWrite: (OutputStream) -> OutputStream = { it }

    override fun openRead(uri: Uri): InputStream {
        readError?.let { throw it }
        return wrapRead(FileInputStream(fileOf(uri)))
    }

    override fun openWrite(uri: Uri, mode: String): OutputStream {
        writeModes += mode
        writeError?.invoke(mode)?.let { throw it }
        return wrapWrite(FileOutputStream(fileOf(uri), false))
    }

    private fun fileOf(uri: Uri): File = File(checkNotNull(uri.path))
}

/** A provider that refuses mode `"wt"` like some document providers (IllegalArgumentException) and accepts `"w"`. */
internal class FailingWriteStreams(private val delegate: FileBackupStreams = FileBackupStreams()) :
    BackupStreams by delegate {
    val writeModes: List<String> get() = delegate.writeModes

    override fun openWrite(uri: Uri, mode: String): OutputStream {
        if (mode == "wt") {
            delegate.writeModes += mode
            throw IllegalArgumentException("Unsupported mode: wt")
        }
        return delegate.openWrite(uri, mode)
    }
}

/**
 * Throws `IOException(message)` once more than [failAfterBytes] bytes were written to it (null: never), and in
 * [close] when [failOnClose].
 */
internal class FailingOutputStream(
    private val out: OutputStream,
    private val failAfterBytes: Long?,
    private val message: String,
    private val failOnClose: Boolean = false,
    private val error: () -> Exception = { IOException(message) },
) : OutputStream() {
    private var written = 0L

    override fun write(b: Int) = write(byteArrayOf(b.toByte()), 0, 1)

    override fun write(b: ByteArray, off: Int, len: Int) {
        if (failAfterBytes != null && written + len > failAfterBytes) throw error()
        out.write(b, off, len)
        written += len
    }

    override fun flush() = out.flush()

    override fun close() {
        out.close()
        if (failOnClose) throw error()
    }
}

/** Calls [hook] once, when [atBytes] bytes have been written through it. */
internal class HookOutputStream(
    private val out: OutputStream,
    private val atBytes: Long,
    private val hook: () -> Unit,
) : OutputStream() {
    var written = 0L
        private set
    private var called = false

    override fun write(b: Int) = write(byteArrayOf(b.toByte()), 0, 1)

    override fun write(b: ByteArray, off: Int, len: Int) {
        out.write(b, off, len)
        written += len
        if (!called && written >= atBytes) {
            called = true
            hook()
        }
    }

    override fun flush() = out.flush()

    override fun close() = out.close()
}

/** Gives the bytes of [input] up to byte [failAfterBytes], then throws `IOException(message)`. */
internal class FailingInputStream(input: InputStream, private val failAfterBytes: Long, private val message: String) :
    FilterInputStream(input) {
    private var position = 0L

    override fun read(): Int {
        val one = ByteArray(1)
        return if (read(one, 0, 1) == -1) -1 else one[0].toInt() and 0xff
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (position >= failAfterBytes) throw IOException(message)
        val count = super.read(b, off, minOf(len.toLong(), failAfterBytes - position).toInt())
        if (count > 0) position += count
        return count
    }

    override fun skip(n: Long): Long = throw IOException("skip not supported")
}

/**
 * One read stops exactly at byte [gateAt], however much a buffer asks for; the next read calls [reached] and waits
 * for [release] (at most 5 seconds) before going on. For the fallback of plan T-003, section 3.4.
 */
internal class GatedInputStream(
    input: InputStream,
    private val gateAt: Long,
    val reached: CountDownLatch = CountDownLatch(1),
    val release: CountDownLatch = CountDownLatch(1),
) : FilterInputStream(input) {
    private var position = 0L

    override fun read(): Int {
        val one = ByteArray(1)
        return if (read(one, 0, 1) == -1) -1 else one[0].toInt() and 0xff
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (position == gateAt && reached.count > 0) {
            reached.countDown()
            release.await(5, TimeUnit.SECONDS)
        }
        val limit = if (position < gateAt) minOf(len.toLong(), gateAt - position).toInt() else len
        val count = super.read(b, off, limit)
        if (count > 0) position += count
        return count
    }

    override fun skip(n: Long): Long = throw IOException("skip not supported")
}

/** Throws [error] at every read. */
internal class ThrowingInputStream(private val error: RuntimeException) : InputStream() {
    override fun read(): Int = throw error

    override fun read(b: ByteArray, off: Int, len: Int): Int = throw error
}

/** Calls [hook] at the first read, then reads [input]. [wasRead] tells whether a read happened. */
internal class HookInputStream(input: InputStream, private val hook: () -> Unit) : FilterInputStream(input) {
    var wasRead = false
        private set

    override fun read(): Int {
        beforeRead()
        return super.read()
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        beforeRead()
        return super.read(b, off, len)
    }

    private fun beforeRead() {
        if (!wasRead) {
            wasRead = true
            hook()
        }
    }
}

/**
 * Wraps a real store; its imports fail on purpose. Turns count from 1. [failWriteFileAt]: the import throws
 * `IOException(message)` at this turn of `writeFile`, before writing. [failCommit]: `commit` moves the projects for
 * real, then throws [ProjectImportCommitException] with the moved ids. [writeFileSuccesses] counts the `writeFile`
 * calls that completed, over every import.
 */
internal class FailingImportStore(private val delegate: ProjectStore) : ProjectStore by delegate {
    var failWriteFileAt: Int? = null
    var message: String = "Could not write"
    var failCommit: Boolean = false

    var writeFileSuccesses = 0
        private set

    override suspend fun beginImport(): ProjectImport {
        val inner = delegate.beginImport()
        return object : ProjectImport {
            private var turns = 0

            override suspend fun writeFile(id: ProjectId, file: ProjectFile, fill: suspend (OutputStream) -> Unit) {
                turns++
                if (turns == failWriteFileAt) throw IOException(message)
                inner.writeFile(id, file, fill)
                writeFileSuccesses++
            }

            override suspend fun write(project: Project) = inner.write(project)

            override suspend fun commit(): List<ProjectId> {
                val moved = inner.commit()
                if (failCommit) throw ProjectImportCommitException(moved, IOException(message))
                return moved
            }

            override suspend fun discard() = inner.discard()
        }
    }
}

/** Wraps a real store; `readFile` throws `IOException(message)` at turn [failReadFileAt] (from 1). */
internal class FailingReadStore(
    private val delegate: ProjectStore,
    private val failReadFileAt: Int,
    private val message: String = "read",
) : ProjectStore by delegate {
    private var turns = 0

    override suspend fun readFile(id: ProjectId, file: ProjectFile): ByteArray? {
        turns++
        if (turns == failReadFileAt) throw IOException(message)
        return delegate.readFile(id, file)
    }
}

/** A repository that only counts [refresh]. */
internal class RefreshCountingRepository : ProjectRepository {
    @Volatile
    var count = 0
        private set

    override val projects: Flow<List<ProjectListItem>> = flowOf(emptyList())
    override val createdCount: Flow<Int> = flowOf(0)

    override suspend fun create(request: NewProject): ProjectResult<Project> = error("not used")

    override suspend fun save(project: Project): ProjectResult<Project> = error("not used")

    override suspend fun rename(id: ProjectId, name: String): ProjectResult<Project> = error("not used")

    override suspend fun duplicate(id: ProjectId): ProjectResult<Project> = error("not used")

    override suspend fun delete(id: ProjectId): ProjectResult<Unit> = error("not used")

    override fun refresh() {
        count++
    }
}

/** Runs nothing until [runAll] is called. Same as the one of StoreFrameRendererTest, which is private there. */
internal class QueueDispatcher : CoroutineDispatcher() {
    private val queue = ArrayDeque<Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        queue.addLast(block)
    }

    fun runAll() {
        while (queue.isNotEmpty()) queue.removeFirst().run()
    }
}

/** A ZIP file in memory with [entries] in order, written by ZipOutputStream (so it has a central directory). */
fun zipOf(vararg entries: Entry): ByteArray = zipOf(entries.toList())

fun zipOf(entries: List<Entry>): ByteArray {
    val bytes = ByteArrayOutputStream()
    ZipOutputStream(bytes).use { zip ->
        for ((name, content) in entries) {
            zip.putNextEntry(ZipEntry(name))
            zip.write(content)
            zip.closeEntry()
        }
    }
    return bytes.toByteArray()
}

/**
 * Local entries only (stored, no central directory), written by hand: allows what ZipOutputStream refuses, such as two
 * entries with the same name. ZipInputStream reads such a file entry by entry.
 */
fun storedZipOf(entries: List<Entry>): ByteArray {
    val out = ByteArrayOutputStream()
    fun short(value: Int) {
        out.write(value and 0xff)
        out.write((value shr 8) and 0xff)
    }
    fun int(value: Long) {
        short((value and 0xffff).toInt())
        short(((value shr 16) and 0xffff).toInt())
    }
    for ((name, content) in entries) {
        val nameBytes = name.toByteArray()
        val crc = CRC32().apply { update(content) }.value
        int(0x04034b50L)
        short(10) // version needed
        short(0) // flags
        short(0) // method: stored
        short(0) // time
        short(0x21) // date: 1980-01-01
        int(crc)
        int(content.size.toLong())
        int(content.size.toLong())
        short(nameBytes.size)
        short(0) // extra length
        out.write(nameBytes)
        out.write(content)
    }
    return out.toByteArray()
}

/** Bytes of `backup-v1.frameloop`, the backup format contract approved with plan T-003. */
fun sampleBytes(): ByteArray =
    checkNotNull(BackupSample::class.java.classLoader?.getResource("backup-v1.frameloop")).readBytes()

/** Every entry of `backup-v1.frameloop`, in order, so a test changes one place and writes it again with [zipOf]. */
fun sampleEntries(): List<Entry> = entriesOf(sampleBytes())

/** Every entry of a ZIP file, in order, read with ZipInputStream. */
fun entriesOf(zip: ByteArray): List<Entry> {
    val entries = mutableListOf<Entry>()
    ZipInputStream(zip.inputStream()).use { input ->
        while (true) {
            val entry = input.nextEntry ?: break
            entries += entry.name to input.readBytes()
        }
    }
    return entries
}

/** [this] with `end.json` written again from the entries counted per project (every entry but `project.json`). */
fun List<Entry>.recountEnd(): List<Entry> {
    val counts = sortedMapOf<Int, Int>()
    for ((name, _) in this) {
        val match = Regex("projects/(\\d+)/(.+)").matchEntire(name) ?: continue
        val index = match.groupValues[1].toInt()
        val isProjectJson = match.groupValues[2] == "project.json"
        counts[index] = (counts[index] ?: 0) + if (isProjectJson) 0 else 1
    }
    return replace("end.json", endJson(counts.values.toList()).toByteArray())
}

/** `end.json` text with these file counts. */
fun endJson(fileCounts: List<Int>): String =
    fileCounts.joinToString(",", """{"projects":[""", "]}") { """{"fileCount":$it}""" }

/** `manifest.json` text. */
fun manifestJson(projects: List<Pair<String, Int>>, version: Int = 1, app: String = "frameloop"): String =
    projects.joinToString(",", """{"app":"$app","backupFormatVersion":$version,"projects":[""", "]}") {
        """{"name":"${it.first}","frameCount":${it.second}}"""
    }

/** [this] with the content of entry [name] replaced. Fails when there is no such entry. */
fun List<Entry>.replace(name: String, content: ByteArray): List<Entry> {
    check(any { it.first == name }) { "No entry $name" }
    return map { if (it.first == name) name to content else it }
}

/** [this] without entry [name]. Fails when there is no such entry. */
fun List<Entry>.without(name: String): List<Entry> {
    check(any { it.first == name }) { "No entry $name" }
    return filterNot { it.first == name }
}
