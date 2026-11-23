package com.vgl.draw.animation.feature.export.encode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.designsystem.R as DesignR
import com.vgl.draw.animation.core.designsystem.theme.WatermarkColors
import com.vgl.draw.animation.feature.export.R
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class WatermarkPainterTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun pillBounds_scaleWithShortEdge() {
        val painter = WatermarkPainter.create(context)

        assertPill(painter.pillBounds(1080, 1920), height = 54f, right = 1048f, bottom = 1888f)
        assertPill(painter.pillBounds(720, 1280), height = 36f, right = 698f, bottom = 1258f)
        assertPill(painter.pillBounds(1920, 1080), height = 54f, right = 1888f, bottom = 1048f)
        assertPill(painter.pillBounds(1080, 1080), height = 54f, right = 1048f, bottom = 1048f)
    }

    @Test
    fun draw_pillTextAndIconColours() {
        val painter = WatermarkPainter.create(context)
        val bitmap = filledBitmap(1080, 1920, WHITE)
        painter.draw(Canvas(bitmap), 1080, 1920)
        val bounds = painter.pillBounds(1080, 1920)
        val h = bounds.height()

        val (px, py) = pillPoint(bounds)
        assertChannels(bitmap.getPixel(px, py), 166, 166, 166)
        val textWhite = countWhite(bitmap, bounds.left + h, bounds.top, bounds.right, bounds.bottom)
        assertTrue("white text pixels: $textWhite", textWhite > 100)
        assertTrue("white icon pixels", countIconWhite(bitmap, bounds) > 50)
        assertEquals(WHITE, bitmap.getPixel(540, 960))
        assertEquals(WHITE, bitmap.getPixel(bounds.left.toInt() - 5, bounds.centerY().toInt()))
        bitmap.recycle()

        // Control: the pill darkens what is under it, so on blue it is dark blue, not a fixed gray.
        val blue = filledBitmap(1080, 1920, BLUE)
        painter.draw(Canvas(blue), 1080, 1920)
        assertChannels(blue.getPixel(px, py), 0, 0, 0xA6)
        blue.recycle()
    }

    @Test
    fun colours_followParameters() {
        val custom = WatermarkPainter(
            text = "Frameloop",
            typeface = context.resources.getFont(DesignR.font.inter_semibold),
            icon = requireNotNull(context.getDrawable(DesignR.drawable.brand_mark)),
            pillArgb = 0x80FF0000.toInt(),
        )
        val bitmap = filledBitmap(720, 720, WHITE)
        custom.draw(Canvas(bitmap), 720, 720)
        val (px, py) = pillPoint(custom.pillBounds(720, 720))
        assertChannels(bitmap.getPixel(px, py), 255, 127, 127)
        bitmap.recycle()

        // Default colours come from the design system.
        val defaults = filledBitmap(720, 720, WHITE)
        WatermarkPainter.create(context).draw(Canvas(defaults), 720, 720)
        val expected = blend(WatermarkColors.PILL, WHITE)
        assertChannels(defaults.getPixel(px, py), red(expected), green(expected), blue(expected))
        defaults.recycle()
    }

    @Test
    fun opaquePill_drawsSolidBlack() {
        val painter = WatermarkPainter.create(context)
        val (px, py) = pillPoint(painter.pillBounds(720, 720))

        val opaque = filledBitmap(720, 720, Color.TRANSPARENT)
        painter.draw(Canvas(opaque), 720, 720, opaquePill = true)
        assertEquals(0xFF000000.toInt(), opaque.getPixel(px, py))
        opaque.recycle()

        val translucent = filledBitmap(720, 720, Color.TRANSPARENT)
        painter.draw(Canvas(translucent), 720, 720, opaquePill = false)
        val pixel = translucent.getPixel(px, py)
        assertTrue("alpha ${alpha(pixel)}", abs(alpha(pixel) - 89) <= 1)
        assertEquals(0, pixel and 0xFFFFFF)
        translucent.recycle()
    }

    @Test
    fun draw_twoSizesInterleaved_independent() {
        val painter = WatermarkPainter.create(context)
        val first = filledBitmap(1080, 1920, WHITE)
        painter.draw(Canvas(first), 1080, 1920)
        val second = filledBitmap(720, 1280, WHITE)
        painter.draw(Canvas(second), 720, 1280)
        val third = filledBitmap(1080, 1920, WHITE)
        painter.draw(Canvas(third), 1080, 1920)

        val bounds = painter.pillBounds(1080, 1920)
        for (y in bounds.top.toInt() until bounds.bottom.toInt() + 1) {
            for (x in bounds.left.toInt() until bounds.right.toInt() + 1) {
                assertEquals("pixel ($x, $y)", first.getPixel(x, y), third.getPixel(x, y))
            }
        }
        val smallIcon = countIconWhite(second, painter.pillBounds(720, 1280))
        assertTrue("white icon pixels at 720 × 1280: $smallIcon", smallIcon > 30)
        first.recycle()
        second.recycle()
        third.recycle()
    }

    @Test
    fun create_usesBrandString() {
        val painter = WatermarkPainter.create(context)

        assertEquals(context.getString(R.string.export_watermark_brand), painter.text)
        assertEquals("Frameloop", painter.text)
    }

    private fun assertPill(bounds: RectF, height: Float, right: Float, bottom: Float) {
        assertEquals(height, bounds.height(), 0.01f)
        assertEquals(right, bounds.right, 0.01f)
        assertEquals(bottom, bounds.bottom, 0.01f)
        val ratio = bounds.width() / bounds.height()
        assertTrue("width is $ratio × height", ratio in 3.3f..4.5f)
    }

    private fun assertChannels(pixel: Int, r: Int, g: Int, b: Int) {
        val message = "pixel #%08X, expected ($r, $g, $b) ± 2".format(pixel)
        assertEquals(message, 255, alpha(pixel))
        assertTrue(message, abs(red(pixel) - r) <= 2)
        assertTrue(message, abs(green(pixel) - g) <= 2)
        assertTrue(message, abs(blue(pixel) - b) <= 2)
    }

    private fun countIconWhite(bitmap: Bitmap, bounds: RectF): Int {
        val h = bounds.height()
        return countWhite(
            bitmap,
            bounds.left + h / 4,
            bounds.centerY() - h / 4,
            bounds.left + 3 * h / 4,
            bounds.centerY() + h / 4,
        )
    }

    private fun countWhite(bitmap: Bitmap, left: Float, top: Float, right: Float, bottom: Float): Int {
        var count = 0
        for (y in top.toInt() until bottom.toInt()) {
            for (x in left.toInt() until right.toInt()) {
                if (bitmap.getPixel(x, y) == WHITE) count++
            }
        }
        return count
    }

    private fun filledBitmap(width: Int, height: Int, color: Int): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }

    // Source-over of a translucent colour on an opaque one.
    private fun blend(top: Int, under: Int): Int {
        val a = alpha(top)
        fun channel(t: Int, u: Int) = (t * a + u * (255 - a) + 127) / 255
        return (0xFF shl 24) or
            (channel(red(top), red(under)) shl 16) or
            (channel(green(top), green(under)) shl 8) or
            channel(blue(top), blue(under))
    }
}
