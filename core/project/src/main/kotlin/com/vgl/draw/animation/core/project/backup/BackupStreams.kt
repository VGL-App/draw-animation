package com.vgl.draw.animation.core.project.backup

import android.content.ContentResolver
import android.net.Uri
import java.io.InputStream
import java.io.OutputStream

/** Opens the streams of a [Uri] chosen by the user. Both throw when the [Uri] cannot be opened. */
internal interface BackupStreams {
    fun openRead(uri: Uri): InputStream

    /** [mode] as in [ContentResolver.openOutputStream], for example `"wt"` (write, truncate) or `"w"`. */
    fun openWrite(uri: Uri, mode: String): OutputStream
}

/**
 * [BackupStreams] through a [ContentResolver]. A picker [Uri] carries a temporary grant, so no permission is needed.
 * A null stream becomes a [java.io.FileNotFoundException]. Never retries: choosing the mode is the caller's job.
 */
internal class ContentResolverBackupStreams(private val resolver: ContentResolver) : BackupStreams {
    override fun openRead(uri: Uri): InputStream = TODO()

    override fun openWrite(uri: Uri, mode: String): OutputStream = TODO()
}
