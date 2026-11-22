package com.vgl.draw.animation.core.project.repository

import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.ProjectKind
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectReadResult
import com.vgl.draw.animation.core.project.storage.ProjectStore
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * [ProjectRepository] on a [ProjectStore]. Disk work runs on the store's own dispatcher.
 *
 * Locks: [writeLock] serializes create, duplicate and the one-time clean-up, so two duplicates get distinct names and
 * the clean-up never deletes the folder of a create in progress. A lock per project serializes save, rename (read
 * then write) and reading the source of a duplicate, so a rename never overwrites a save made meanwhile. [writeLock]
 * is always taken before a project lock.
 *
 * Logs never hold a project name, id or exception message (which may hold a path with the id): only the failure kind
 * and the exception class.
 */
internal class DefaultProjectRepository(
    private val store: ProjectStore,
    private val counter: CreatedProjectsCounter,
    private val clock: () -> Long,
    private val logger: AppLogger,
) : ProjectRepository {
    private val version = MutableStateFlow(0)
    private val writeLock = Mutex()
    private val projectLocks = ConcurrentHashMap<ProjectId, Mutex>()
    private val cleanedUp = AtomicBoolean(false)

    // No distinctUntilChanged: refresh() must always emit again.
    @OptIn(ExperimentalCoroutinesApi::class)
    override val projects: Flow<List<ProjectListItem>> = version.mapLatest {
        cleanUpOnce()
        loadAll()
    }

    override val createdCount: Flow<Int> = counter.count

    override suspend fun create(request: NewProject): ProjectResult<Project> {
        // Both checks throw before anything touches the disk.
        val name = ProjectNames.normalize(request.name)
        val photo = (request.background as? NewBackground.Photo)?.let {
            ProjectFile.Background(it.extension) to it.bytes
        }
        val background = when (val requested = request.background) {
            is NewBackground.Solid -> Background.Solid(requested.color)
            is NewBackground.Photo -> Background.Photo(requested.extension)
        }
        return writeLock.withLock {
            val project = Project.create(
                id = ProjectId.random(),
                name = name,
                aspectRatio = request.aspectRatio,
                resolution = request.resolution,
                frameRate = request.frameRate,
                background = background,
                firstLayerName = request.firstLayerName,
                nowMillis = clock(),
                kind = request.kind,
            )
            try {
                if (photo != null) store.writeFile(project.id, photo.first, photo.second)
                store.write(project)
            } catch (e: IOException) {
                return@withLock writeFailure("Project not created", e, removeOnFailure = project.id)
            }
            if (request.kind == ProjectKind.Regular) {
                try {
                    counter.increment()
                } catch (e: IOException) {
                    // The project is on the device: still a success. The next default name may repeat a number.
                    logger.warn(TAG, "Created projects count not increased (${e::class.simpleName})")
                }
            }
            logger.info(TAG, "Project created")
            changed()
            ProjectResult.Success(project)
        }
    }

    override suspend fun save(project: Project): ProjectResult<Project> = withProjectLock(project.id) {
        val saved = project.copy(updatedAtMillis = clock())
        try {
            store.write(saved)
            changed()
            ProjectResult.Success(saved)
        } catch (e: IOException) {
            writeFailure("Project not saved", e, removeOnFailure = null)
        }
    }

    override suspend fun rename(id: ProjectId, name: String): ProjectResult<Project> {
        val normalized = ProjectNames.normalize(name)
        return withProjectLock(id) {
            when (val result = store.read(id)) {
                ProjectReadResult.NotFound -> ProjectResult.Failure(ProjectFailure.NOT_FOUND)
                is ProjectReadResult.Corrupt, is ProjectReadResult.NewerVersion ->
                    ProjectResult.Failure(ProjectFailure.UNREADABLE)
                is ProjectReadResult.Ok -> {
                    // updatedAtMillis kept: renaming does not move the project to the top (plan T-002, question 2).
                    val renamed = result.project.copy(name = normalized)
                    try {
                        store.write(renamed)
                        logger.info(TAG, "Project renamed")
                        changed()
                        ProjectResult.Success(renamed)
                    } catch (e: IOException) {
                        writeFailure("Project not renamed", e, removeOnFailure = null)
                    }
                }
            }
        }
    }

    override suspend fun duplicate(id: ProjectId): ProjectResult<Project> = writeLock.withLock {
        val existingNames = loadAll().mapNotNull { it.nameOrNull() }
        withProjectLock(id) { duplicateLocked(id, existingNames) }
    }

    override suspend fun delete(id: ProjectId): ProjectResult<Unit> = try {
        store.delete(id)
        logger.info(TAG, "Project deleted")
        changed()
        ProjectResult.Success(Unit)
    } catch (e: IOException) {
        writeFailure("Project not deleted", e, removeOnFailure = null)
    }

    override fun refresh() {
        changed()
    }

    private suspend fun duplicateLocked(id: ProjectId, existingNames: List<String>): ProjectResult<Project> {
        val source = when (val result = store.read(id)) {
            ProjectReadResult.NotFound -> return ProjectResult.Failure(ProjectFailure.NOT_FOUND)
            is ProjectReadResult.Corrupt, is ProjectReadResult.NewerVersion ->
                return ProjectResult.Failure(ProjectFailure.UNREADABLE)
            is ProjectReadResult.Ok -> result.project
        }
        val now = clock()
        val copy = source.copy(
            id = ProjectId.random(),
            name = ProjectNames.copyName(source.name, existingNames),
            createdAtMillis = now,
            updatedAtMillis = now,
        )
        try {
            // Files first, project.json last: a copy stopped part way is never listed.
            for (file in referencedFiles(source)) {
                val bytes = store.readFile(source.id, file) ?: continue
                store.writeFile(copy.id, file, bytes)
            }
            store.write(copy)
        } catch (e: IOException) {
            return writeFailure("Project not duplicated", e, removeOnFailure = copy.id)
        }
        logger.info(TAG, "Project duplicated")
        changed()
        return ProjectResult.Success(copy)
    }

    private suspend fun loadAll(): List<ProjectListItem> {
        val readable = mutableListOf<Project>()
        val unreadable = mutableListOf<ProjectListItem.Unreadable>()
        for (id in store.ids()) {
            when (val result = store.read(id)) {
                is ProjectReadResult.Ok -> if (result.project.isUserProject) readable += result.project
                is ProjectReadResult.Corrupt -> unreadable += ProjectListItem.Unreadable(id, result.name, false)
                is ProjectReadResult.NewerVersion -> unreadable += ProjectListItem.Unreadable(id, result.name, true)
                // Deleted between ids() and read().
                ProjectReadResult.NotFound -> Unit
            }
        }
        val sorted = readable.sortedWith(compareByDescending<Project> { it.updatedAtMillis }.thenBy { it.id.value })
        return sorted.map { ProjectListItem.Readable(it) } + unreadable.sortedBy { it.id.value }
    }

    private suspend fun cleanUpOnce() {
        if (cleanedUp.get()) return
        writeLock.withLock {
            if (!cleanedUp.compareAndSet(false, true)) return
            try {
                val removed = store.deleteIncomplete()
                if (removed > 0) logger.info(TAG, "Removed $removed incomplete project folders")
            } catch (e: IOException) {
                logger.warn(TAG, "Incomplete project folders not removed (${e::class.simpleName})")
            }
        }
    }

    /** Removes the half-written project [removeOnFailure], logs, and returns the failure kind. */
    private suspend fun writeFailure(
        action: String,
        e: IOException,
        removeOnFailure: ProjectId?,
    ): ProjectResult.Failure {
        if (removeOnFailure != null) {
            try {
                store.delete(removeOnFailure)
            } catch (cleanUpError: IOException) {
                logger.warn(TAG, "Half-written project not removed (${cleanUpError::class.simpleName})")
            }
        }
        val reason = if (isOutOfSpace(e)) ProjectFailure.NO_SPACE else ProjectFailure.WRITE_FAILED
        logger.warn(TAG, "$action: $reason (${e::class.simpleName})")
        return ProjectResult.Failure(reason)
    }

    private suspend inline fun <T> withProjectLock(id: ProjectId, block: () -> T): T =
        projectLocks.computeIfAbsent(id) { Mutex() }.withLock(action = block)

    private fun changed() {
        version.update { it + 1 }
    }

    private fun ProjectListItem.nameOrNull(): String? = when (this) {
        is ProjectListItem.Readable -> project.name
        is ProjectListItem.Unreadable -> name
    }

    private fun referencedFiles(project: Project): List<ProjectFile> = buildList {
        add(ProjectFile.Thumbnail)
        (project.background as? Background.Photo)?.let { add(ProjectFile.Background(it.fileExtension)) }
        for (frame in project.frames) {
            for (layer in project.layers) add(ProjectFile.Cell(frame.id, layer.id))
        }
        project.sounds.forEach { add(ProjectFile.Sound(it.id, it.fileExtension)) }
        project.reference?.let { reference ->
            repeat(reference.frameCount) { add(ProjectFile.ReferenceImage(it, reference.fileExtension)) }
        }
    }

    private companion object {
        const val TAG = "ProjectRepo"
    }
}
