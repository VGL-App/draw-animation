package com.vgl.draw.animation.feature.projects.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import java.io.File
import java.io.IOException
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [PhotoPreparer] and its pure functions. NATIVE graphics: real JPEG and PNG encoding and decoding, and real EXIF
 * (plan T-011, section 4.1). Files are opened through `file://` uris, as the content resolver opens a picker's uri.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "xxhdpi")
class PhotoPreparerTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val logger = RecordingAppLogger()
    private val preparer = PhotoPreparer(ApplicationProvider.getApplicationContext(), logger)

    private fun uriOf(file: File): String = Uri.fromFile(file).toString()

    private fun write(bitmap: Bitmap, format: Bitmap.CompressFormat, name: String): File {
        val file = folder.newFile(name)
        file.outputStream().use { bitmap.compress(format, 100, it) }
        return file
    }

    private fun jpegWithOrientation(bitmap: Bitmap, orientation: Int): File {
        val file = write(bitmap, Bitmap.CompressFormat.JPEG, "photo_$orientation.jpg")
        ExifInterface(file.path).apply {
            setAttribute(ExifInterface.TAG_ORIENTATION, orientation.toString())
            saveAttributes()
        }
        return file
    }

    /** 40 x 20: left half red, right half blue. */
    private fun halves(): Bitmap = Bitmap.createBitmap(40, 20, Bitmap.Config.ARGB_8888).apply {
        val canvas = Canvas(this)
        canvas.drawRect(0f, 0f, 20f, 20f, Paint().apply { color = Color.RED })
        canvas.drawRect(20f, 0f, 40f, 20f, Paint().apply { color = Color.BLUE })
    }

    /** 40 x 20, each corner 20 x 10: red top left, green top right, blue bottom left, yellow bottom right. */
    private fun corners(): Bitmap = Bitmap.createBitmap(40, 20, Bitmap.Config.ARGB_8888).apply {
        val canvas = Canvas(this)
        canvas.drawRect(0f, 0f, 20f, 10f, Paint().apply { color = Color.RED })
        canvas.drawRect(20f, 0f, 40f, 10f, Paint().apply { color = Color.GREEN })
        canvas.drawRect(0f, 10f, 20f, 20f, Paint().apply { color = Color.BLUE })
        canvas.drawRect(20f, 10f, 40f, 20f, Paint().apply { color = Color.YELLOW })
    }

    private fun decode(photo: PreparedPhoto): Bitmap =
        requireNotNull(BitmapFactory.decodeByteArray(photo.bytes, 0, photo.bytes.size)) { "bytes are not an image" }

    private fun assertColor(message: String, expected: Int, actual: Int, tolerance: Int = JPEG_TOLERANCE) {
        val close = abs(Color.red(expected) - Color.red(actual)) <= tolerance &&
            abs(Color.green(expected) - Color.green(actual)) <= tolerance &&
            abs(Color.blue(expected) - Color.blue(actual)) <= tolerance
        assertTrue("$message: expected #%08X, was #%08X".format(expected, actual), close)
    }

    @Test
    fun exifTransform_allOrientations() {
        val expected = mapOf(
            ExifInterface.ORIENTATION_NORMAL to PhotoTransform(0, false),
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL to PhotoTransform(0, true),
            ExifInterface.ORIENTATION_ROTATE_180 to PhotoTransform(180, false),
            ExifInterface.ORIENTATION_FLIP_VERTICAL to PhotoTransform(180, true),
            ExifInterface.ORIENTATION_TRANSPOSE to PhotoTransform(270, true),
            ExifInterface.ORIENTATION_ROTATE_90 to PhotoTransform(90, false),
            ExifInterface.ORIENTATION_TRANSVERSE to PhotoTransform(90, true),
            ExifInterface.ORIENTATION_ROTATE_270 to PhotoTransform(270, false),
        )
        for ((orientation, transform) in expected) {
            assertEquals("orientation $orientation", transform, exifTransform(orientation))
        }
        assertEquals(PhotoTransform(0, false), exifTransform(ExifInterface.ORIENTATION_UNDEFINED))
        assertEquals(PhotoTransform(0, false), exifTransform(42))
    }

    @Test
    fun photoSampleSize_examples() {
        assertEquals(1, photoSampleSize(4032, 3024))
        assertEquals(2, photoSampleSize(8000, 2000))
        assertEquals(4, photoSampleSize(12000, 9000))
        assertEquals(1, photoSampleSize(1000, 800))
        assertEquals(2, photoSampleSize(2000, 8000))
    }

    @Test
    fun photoTargetSize_examples() {
        assertEquals(3840 to 2880, photoTargetSize(4032, 3024))
        assertEquals(3840 to 960, photoTargetSize(4000, 1000))
        assertEquals(3000 to 2250, photoTargetSize(3000, 2250))
        assertEquals(960 to 3840, photoTargetSize(1000, 4000))
    }

    @Test
    fun exifReadFails_usesOrientation1() {
        assertEquals(1, orientationOrDefault(logger) { throw IOException("broken") })
        val warnings = logger.entries.filter { it.level == LogLevel.WARN }
        assertEquals(1, warnings.size)
        assertTrue(warnings.single().message, "EXIF not readable" in warnings.single().message)

        // Control: a readable orientation is returned as it is, with nothing logged.
        val quiet = RecordingAppLogger()
        assertEquals(6, orientationOrDefault(quiet) { 6 })
        assertEquals(0, quiet.entries.size)

        assertEquals(1, orientationOrDefault(quiet) { throw IllegalStateException("broken") })
        assertEquals(1, quiet.entries.size)
    }

    @Test
    fun prepare_rotatesByExif() {
        val rotated = decode(prepared(preparer.prepare(uriOf(jpegWithOrientation(halves(), 6)))))
        assertEquals(20, rotated.width)
        assertEquals(40, rotated.height)
        assertColor("top", Color.RED, rotated.getPixel(10, 5))
        assertColor("bottom", Color.BLUE, rotated.getPixel(10, 35))

        // Control: orientation 1 keeps the image as it is.
        val kept = decode(prepared(preparer.prepare(uriOf(jpegWithOrientation(halves(), 1)))))
        assertEquals(40, kept.width)
        assertEquals(20, kept.height)
        assertColor("left", Color.RED, kept.getPixel(10, 10))
        assertColor("right", Color.BLUE, kept.getPixel(30, 10))
    }

    @Test
    fun prepare_mirroredOrientation() {
        // 5: mirrored, then rotated 270 (a transpose): the corners swap across the main diagonal.
        val transposed = decode(prepared(preparer.prepare(uriOf(jpegWithOrientation(corners(), 5)))))
        assertEquals(20, transposed.width)
        assertEquals(40, transposed.height)
        assertColor("top left", Color.RED, transposed.getPixel(5, 10))
        assertColor("top right", Color.BLUE, transposed.getPixel(15, 10))
        assertColor("bottom left", Color.GREEN, transposed.getPixel(5, 30))
        assertColor("bottom right", Color.YELLOW, transposed.getPixel(15, 30))

        // Control: 8 only rotates; two corners land elsewhere than with 5.
        val rotated = decode(prepared(preparer.prepare(uriOf(jpegWithOrientation(corners(), 8)))))
        assertEquals(20, rotated.width)
        assertEquals(40, rotated.height)
        assertColor("top left", Color.GREEN, rotated.getPixel(5, 10))
        assertColor("top right", Color.YELLOW, rotated.getPixel(15, 10))
        assertColor("bottom left", Color.RED, rotated.getPixel(5, 30))
        assertColor("bottom right", Color.BLUE, rotated.getPixel(15, 30))
    }

    @Test
    fun prepare_scalesLongSideTo3840() {
        val wide = Bitmap.createBitmap(8000, 200, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GRAY) }
        val scaled = decode(prepared(preparer.prepare(uriOf(write(wide, Bitmap.CompressFormat.PNG, "wide.png")))))
        assertEquals(3840, scaled.width)
        assertEquals(96, scaled.height)

        // A small image keeps its size; it has no EXIF and is still used.
        val small = Bitmap.createBitmap(1000, 800, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GRAY) }
        val kept = decode(prepared(preparer.prepare(uriOf(write(small, Bitmap.CompressFormat.PNG, "small.png")))))
        assertEquals(1000, kept.width)
        assertEquals(800, kept.height)
    }

    @Test
    fun prepare_transparentBecomesWhite() {
        val clear = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.TRANSPARENT) }
        val photo = prepared(preparer.prepare(uriOf(write(clear, Bitmap.CompressFormat.PNG, "clear.png"))))
        assertEquals("jpg", photo.extension)
        val pixel = decode(photo).getPixel(5, 5)
        assertTrue(
            "#%08X".format(pixel),
            Color.red(pixel) >= 245 && Color.green(pixel) >= 245 && Color.blue(pixel) >= 245,
        )
    }

    @Test
    fun prepare_notAnImage_returnsNull() {
        val file = folder.newFile("notes.txt").apply { writeText("not an image at all") }
        assertNull(preparer.prepare(uriOf(file)))
        assertNotUsableLogged(file)
    }

    @Test
    fun prepare_missingFile_returnsNull() {
        val file = File(folder.root, "missing_photo.jpg")
        assertNull(preparer.prepare(uriOf(file)))
        assertNotUsableLogged(file)
    }

    @Test
    fun preview_is48dpForDensity() {
        val image = Bitmap.createBitmap(1000, 800, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GRAY) }
        val photo = prepared(preparer.prepare(uriOf(write(image, Bitmap.CompressFormat.PNG, "preview.png"))))
        assertEquals(144, photo.preview.width)
        assertEquals(144, photo.preview.height)
    }

    private fun assertNotUsableLogged(file: File) {
        val warnings = logger.entries.filter { it.level == LogLevel.WARN }
        assertEquals(warnings.toString(), 1, warnings.size)
        val message = warnings.single().message
        assertTrue(message, "Photo not usable" in message)
        assertTrue(message, file.name !in message && folder.root.path !in message)
    }

    private fun prepared(value: PreparedPhoto?): PreparedPhoto = requireNotNull(value) { "photo not prepared" }

    private companion object {
        const val JPEG_TOLERANCE = 40
    }
}
