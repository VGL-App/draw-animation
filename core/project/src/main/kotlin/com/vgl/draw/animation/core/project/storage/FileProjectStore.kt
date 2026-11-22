package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** [ProjectStore] on a folder of the device, [root] holding one folder per project. */
internal class FileProjectStore(
    private val root: File,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: AppLogger,
) : ProjectStore {
    private val locks = ConcurrentHashMap<ProjectId, Mutex>()

    override suspend fun ids(): List<ProjectId> = withContext(ioDispatcher) {
        val children = root.listFiles() ?: return@withContext emptyList()
        children
            .filter { it.isDirectory && !it.name.startsWith(".") && File(it, METADATA_FILE).exists() }
            .mapNotNull { ProjectId.parseOrNull(it.name) }
            .sortedBy { it.value }
    }

    override suspend fun read(id: ProjectId): ProjectReadResult = withContext(ioDispatcher) {
        val file = File(projectDir(id), METADATA_FILE).also { checkInsideRoot(it) }
        if (!file.exists()) return@withContext ProjectReadResult.NotFound
        val result = try {
            decode(id, file.readText())
        } catch (e: IOException) {
            ProjectReadResult.Corrupt(name = null, reason = CorruptReason.UNREADABLE_FILE)
        }
        when (result) {
            is ProjectReadResult.Corrupt -> logger.warn(TAG, "Project unreadable: ${result.reason}")
            is ProjectReadResult.NewerVersion -> logger.warn(TAG, "Project has newer format ${result.version}")
            else -> Unit
        }
        result
    }

    override suspend fun write(project: Project) {
        // Encoding first: an invalid project throws before anything touches the disk.
        val bytes = ProjectFormat.encode(project).toByteArray(Charsets.UTF_8)
        withProjectLock(project.id) {
            writeAtomically(File(projectDir(project.id), METADATA_FILE), bytes)
        }
    }

    override suspend fun readFile(id: ProjectId, file: ProjectFile): ByteArray? = withContext(ioDispatcher) {
        val target = fileOf(id, file)
        if (target.exists()) target.readBytes() else null
    }

    override suspend fun writeFile(id: ProjectId, file: ProjectFile, bytes: ByteArray) = withProjectLock(id) {
        writeAtomically(fileOf(id, file), bytes)
    }

    override suspend fun deleteFile(id: ProjectId, file: ProjectFile) = withProjectLock(id) {
        val target = fileOf(id, file)
        if (target.exists() && !target.delete()) throw IOException("Could not delete a project file")
    }

    override suspend fun delete(id: ProjectId) = withProjectLock(id) {
        val dir = projectDir(id)
        val deleting = File(root, "$DELETING_PREFIX${id.value}").also { checkInsideRoot(it) }
        deleteRecursively(deleting)
        if (dir.exists()) {
            if (!dir.renameTo(deleting)) throw IOException("Could not move a project away before deleting it")
            deleteRecursively(deleting)
        }
        // Leftovers of other projects are already hidden from ids(), so failing to clean them is not an error.
        root.listFiles().orEmpty().filter { it.name.startsWith(DELETING_PREFIX) }.forEach { it.deleteRecursively() }
    }

    override suspend fun deleteIncomplete(): Int = withContext(ioDispatcher) {
        val children = root.listFiles() ?: return@withContext 0
        // Names starting with "." never parse as an id, so `.deleting-*` and restore staging folders stay untouched.
        val incomplete = children
            .filter { it.isDirectory && !File(it, METADATA_FILE).exists() }
            .mapNotNull { ProjectId.parseOrNull(it.name) }
        var deleted = 0
        for (id in incomplete) {
            locks.computeIfAbsent(id) { Mutex() }.withLock {
                val dir = projectDir(id)
                // Checked again under the lock: the project may have been completed meanwhile.
                if (dir.isDirectory && !File(dir, METADATA_FILE).exists()) {
                    deleteRecursively(dir)
                    deleted++
                }
            }
        }
        deleted
    }

    private fun decode(id: ProjectId, text: String): ProjectReadResult {
        val result = ProjectFormat.decode(text)
        if (result is ProjectReadResult.Ok && result.project.id != id) {
            return ProjectReadResult.Corrupt(name = result.project.name, reason = CorruptReason.INVALID_CONTENT)
        }
        return result
    }

    private suspend fun <T> withProjectLock(id: ProjectId, block: () -> T): T = withContext(ioDispatcher) {
        locks.computeIfAbsent(id) { Mutex() }.withLock { block() }
    }

    private fun projectDir(id: ProjectId): File = File(root, id.value)

    private fun fileOf(id: ProjectId, file: ProjectFile): File =
        File(projectDir(id), file.relativePath).also { checkInsideRoot(it) }

    /**
     * Second line of defense after the id and extension checks. Both sides are made canonical and compared as paths,
     * because the root may go through a symbolic link (`filesDir` on some devices, the temp folder on macOS).
     */
    private fun checkInsideRoot(target: File) {
        val rootPath = root.canonicalFile.toPath()
        val targetPath = target.canonicalFile.toPath()
        require(targetPath != rootPath && targetPath.startsWith(rootPath)) { "Path outside the projects folder" }
    }

    /**
     * Writes `<name>.tmp` next to [target], syncs it to the disk, then renames it over [target]. If the app stops part
     * way, [target] keeps its previous whole content.
     */
    private fun writeAtomically(target: File, bytes: ByteArray) {
        checkInsideRoot(target)
        val parent = checkNotNull(target.parentFile)
        if (!parent.isDirectory && !parent.mkdirs()) throw IOException("Could not create a project folder")
        val temp = File(parent, target.name + TEMP_SUFFIX)
        try {
            FileOutputStream(temp).use { stream ->
                stream.write(bytes)
                stream.fd.sync()
            }
            if (!temp.renameTo(target)) throw IOException("Could not replace a project file")
        } catch (e: IOException) {
            temp.delete()
            throw e
        }
    }

    private fun deleteRecursively(dir: File) {
        dir.deleteRecursively()
        if (dir.exists()) throw IOException("Could not delete a project folder")
    }

    private companion object {
        const val TAG = "Project"
        const val METADATA_FILE = "project.json"
        const val TEMP_SUFFIX = ".tmp"
        const val DELETING_PREFIX = ".deleting-"
    }
}
