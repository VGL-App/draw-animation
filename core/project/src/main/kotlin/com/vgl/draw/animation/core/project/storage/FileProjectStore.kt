package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher

internal class FileProjectStore(
    private val root: File,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: AppLogger,
) : ProjectStore {
    override suspend fun ids(): List<ProjectId> = TODO()

    override suspend fun read(id: ProjectId): ProjectReadResult = TODO()

    override suspend fun write(project: Project): Unit = TODO()

    override suspend fun readFile(id: ProjectId, file: ProjectFile): ByteArray? = TODO()

    override suspend fun writeFile(id: ProjectId, file: ProjectFile, bytes: ByteArray): Unit = TODO()

    override suspend fun deleteFile(id: ProjectId, file: ProjectFile): Unit = TODO()

    override suspend fun delete(id: ProjectId): Unit = TODO()
}
