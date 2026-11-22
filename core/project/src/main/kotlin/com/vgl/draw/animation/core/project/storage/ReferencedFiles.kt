package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.Project

/**
 * Every file [this] project may have next to `project.json`, in backup order: thumbnail, photo background, sounds,
 * reference images, then cells by frame and, within a frame, by layer. A file in the list may not exist (an empty
 * cell, no thumbnail yet).
 */
internal fun Project.referencedFiles(): List<ProjectFile> = TODO()
