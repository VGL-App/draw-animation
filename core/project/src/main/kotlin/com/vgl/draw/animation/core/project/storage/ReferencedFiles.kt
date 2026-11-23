package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.Project

/**
 * Every file [this] project may have next to `project.json`, in backup order: thumbnail, photo background, sounds,
 * reference images, then cells by frame and, within a frame, by layer. A file in the list may not exist (an empty
 * cell, no thumbnail yet).
 */
internal fun Project.referencedFiles(): List<ProjectFile> = buildList {
    add(ProjectFile.Thumbnail)
    (background as? Background.Photo)?.let { add(ProjectFile.Background(it.fileExtension)) }
    sounds.forEach { add(ProjectFile.Sound(it.id, it.fileExtension)) }
    reference?.let { layer -> repeat(layer.frameCount) { add(ProjectFile.ReferenceImage(it, layer.fileExtension)) } }
    for (frame in frames) {
        for (layer in layers) add(ProjectFile.Cell(frame.id, layer.id))
    }
}
