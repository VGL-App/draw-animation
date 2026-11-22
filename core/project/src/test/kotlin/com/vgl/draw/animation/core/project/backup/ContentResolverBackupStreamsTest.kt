package com.vgl.draw.animation.core.project.backup

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import java.io.File
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The real ContentResolver of Robolectric, with `file://` uris. */
@RunWith(RobolectricTestRunner::class)
class ContentResolverBackupStreamsTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val streams by lazy {
        ContentResolverBackupStreams(ApplicationProvider.getApplicationContext<Context>().contentResolver)
    }

    @Test
    fun write_truncatesExistingFile() {
        val file = File(tmp.root, "out.frameloop")
        val uri = Uri.fromFile(file)

        streams.openWrite(uri, "wt").use { it.write(ByteArray(200_000) { index -> index.toByte() }) }
        assertEquals(200_000L, file.length())
        streams.openWrite(uri, "wt").use { it.write(ByteArray(10) { 7 }) }

        assertEquals(10L, file.length())
    }

    @Test
    fun read_roundTrips() {
        val file = File(tmp.root, "in.frameloop")
        val bytes = ByteArray(5_000) { (it * 31).toByte() }
        streams.openWrite(Uri.fromFile(file), "wt").use { it.write(bytes) }

        val read = streams.openRead(Uri.fromFile(file)).use { it.readBytes() }

        assertArrayEquals(bytes, read)
    }
}
