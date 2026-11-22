package com.vgl.draw.animation.core.project.backup

import android.net.Uri
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.storage.ProjectStore
import kotlinx.coroutines.CoroutineDispatcher

/** [ProjectBackup] on a [ProjectStore], see [BackupFormat] for the file. [newId] gives the id of each restored project. */
internal class DefaultProjectBackup(
    private val store: ProjectStore,
    private val repository: ProjectRepository,
    private val streams: BackupStreams,
    private val clock: () -> Long,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: AppLogger,
    private val newId: () -> ProjectId = { ProjectId.random() },
) : ProjectBackup {
    override suspend fun export(target: Uri, ids: List<ProjectId>): BackupExportResult = TODO()

    override suspend fun exportAll(target: Uri): BackupExportResult = TODO()

    override suspend fun restore(source: Uri, onProgress: suspend (RestoreProgress) -> Unit): RestoreResult = TODO()
}
