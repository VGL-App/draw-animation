package com.vgl.draw.animation.core.project.repository

import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.storage.ProjectStore
import kotlinx.coroutines.flow.Flow

internal class DefaultProjectRepository(
    private val store: ProjectStore,
    private val counter: CreatedProjectsCounter,
    private val clock: () -> Long,
    private val logger: AppLogger,
) : ProjectRepository {
    override val projects: Flow<List<ProjectListItem>> get() = TODO()

    override val createdCount: Flow<Int> get() = TODO()

    override suspend fun create(request: NewProject): ProjectResult<Project> = TODO()

    override suspend fun save(project: Project): ProjectResult<Project> = TODO()

    override suspend fun rename(id: ProjectId, name: String): ProjectResult<Project> = TODO()

    override suspend fun duplicate(id: ProjectId): ProjectResult<Project> = TODO()

    override suspend fun delete(id: ProjectId): ProjectResult<Unit> = TODO()

    override fun refresh(): Unit = TODO()
}
