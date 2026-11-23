package com.vgl.draw.animation.feature.export.model

import com.vgl.draw.animation.core.project.model.Resolution
import java.io.File

/** What the user chose on the Export screen. */
data class ExportOptions(val format: ExportFormat, val resolution: Resolution, val watermark: Boolean)

/** Files written into the work directory, in frame order, and their total size. */
data class ExportedFiles(val files: List<File>, val totalBytes: Long)
