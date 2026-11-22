package com.vgl.draw.animation.core.lessons.content

import java.io.File

data class LessonLayer(val name: String)

/** One frame of the lesson project; [hold] is how many playback ticks it stays on screen. */
data class LessonFrame(val hold: Int)

/**
 * One step of the lesson: the user draws on [frame] and [layer] (indexes from 0). With [hasGuide], a red guide image
 * covers the frame; [demoFrameCount] images make the "Show me" clip. Texts are English only (spec lessons, question 5).
 */
data class LessonStep(
    val title: String,
    val instruction: String,
    val frame: Int,
    val layer: Int,
    val hasGuide: Boolean,
    val demoFrameCount: Int,
)

/**
 * The content of a lesson pack, extracted in [dir]. Raw values ([aspectRatio] "9:16", [resolution] "720p",
 * [frameRate], [background] "white") are the format values of :core:project; images have the canvas size.
 */
data class LessonPack(
    val lessonId: String,
    val contentVersion: String,
    val aspectRatio: String,
    val resolution: String,
    val frameRate: Int,
    val background: String,
    val layers: List<LessonLayer>,
    val frames: List<LessonFrame>,
    val steps: List<LessonStep>,
    val dir: File,
) {
    /** Prebuilt content of [layer] at [frame], or null for an empty cell. */
    fun cellFile(frame: Int, layer: Int): File? = TODO()

    /** Guide image of [step], or null when the step has no guide. */
    fun guideFile(step: Int): File? = TODO()

    /** Images of the "Show me" clip of [step], in play order; empty when the step has none. */
    fun demoFiles(step: Int): List<File> = TODO()
}
