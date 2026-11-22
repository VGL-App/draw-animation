package com.vgl.draw.animation.core.project.backup

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** File names suggested to the system file picker for a backup. */
object BackupFileNames {
    const val EXTENSION = "frameloop"

    /** `.frameloop` has no registered type, so files are created as plain binary data. */
    const val BACKUP_MIME_TYPE = "application/octet-stream"

    /**
     * `"<name>.frameloop"`, with the characters not allowed in file names (`/ \ : * ? " < > |` and control characters)
     * replaced by `_`; `"Frameloop.frameloop"` when nothing but spaces is left.
     */
    fun forProject(name: String): String {
        val safe = name.map { if (it in FORBIDDEN || it.isISOControl()) '_' else it }.joinToString("")
        return "${safe.ifBlank { APP_NAME }}.$EXTENSION"
    }

    /** `"Frameloop-backup-YYYY-MM-DD.frameloop"`. */
    fun forAll(date: LocalDate): String = "$APP_NAME-backup-${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}.$EXTENSION"

    private const val APP_NAME = "Frameloop"
    private const val FORBIDDEN = "/\\:*?\"<>|"
}
