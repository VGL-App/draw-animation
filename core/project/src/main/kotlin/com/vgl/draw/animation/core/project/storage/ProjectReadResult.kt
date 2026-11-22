package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.Project

/** Outcome of reading a project. Reading never throws. */
sealed interface ProjectReadResult {
    data class Ok(val project: Project) : ProjectReadResult

    /** The project has no `project.json`. */
    data object NotFound : ProjectReadResult

    /** The project cannot be read. [name] is kept when the `name` field is still readable. */
    data class Corrupt(val name: String?, val reason: CorruptReason) : ProjectReadResult

    /** Saved by a newer version of the app, with format [version]. */
    data class NewerVersion(val version: Int, val name: String?) : ProjectReadResult
}

enum class CorruptReason { UNREADABLE_FILE, INVALID_JSON, MISSING_VERSION, UNKNOWN_VERSION, INVALID_CONTENT }
