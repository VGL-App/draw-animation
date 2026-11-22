package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BackgroundDecoderTest {
    @Test
    fun largePhoto_isDownsampled_andCropsCenter() {
        val bytes = RenderTestImages.png(largeStripes())

        val photo = checkNotNull(BackgroundDecoder.decode(bytes, 100, 100))
        assertEquals(500, photo.width)
        assertEquals(125, photo.height)

        val frame = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        FrameComposer.drawPhoto(Canvas(frame), 100, 100, photo, TEST_PALETTE.white, CAMERA_ORIGIN)
        assertEquals(RED, frame.getPixel(5, 50))
        assertEquals(RED, frame.getPixel(94, 50))

        assertNull(BackgroundDecoder.decode("not a png".toByteArray(), 100, 100))
        assertEquals(1, BackgroundDecoder.sampleSize(100, 100, 100, 100))
    }

    companion object {
        /** 4000 × 1000: `GREEN` 0 to 1500, `RED` 1500 to 2500, `BLUE_INK` 2500 to 4000. */
        fun largeStripes(): Bitmap =
            RenderTestImages.verticalStripes(1000, 1500 to GREEN, 1000 to RED, 1500 to BLUE_INK)
    }
}
