package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId

/** Projects saved on the device, one folder per project. */
interface ProjectStore {
    suspend fun ids(): List<ProjectId>

    suspend fun read(id: ProjectId): ProjectReadResult

    suspend fun write(project: Project)

    suspend fun readFile(id: ProjectId, file: ProjectFile): ByteArray?

    suspend fun writeFile(id: ProjectId, file: ProjectFile, bytes: ByteArray)

    suspend fun deleteFile(id: ProjectId, file: ProjectFile)

    suspend fun delete(id: ProjectId)
}
