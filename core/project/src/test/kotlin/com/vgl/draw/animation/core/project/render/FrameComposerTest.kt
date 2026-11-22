package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.project.model.CameraKey
import com.vgl.draw.animation.core.project.render.RenderTestImages.alpha
import com.vgl.draw.animation.core.project.render.RenderTestImages.cell
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class FrameComposerTest {
    private fun frame(width: Int = 100, height: Int = 100): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    private fun key(panX: Float = 0f, panY: Float = 0f, zoom: Float = 1f, rotation: Float = 0f) =
        CameraKey(panX = panX, panY = panY, zoom = zoom, rotationDegrees = rotation)

    private fun drawLayer(cell: Bitmap, camera: CameraKey, size: Int = 100): Bitmap = frame(size, size).also {
        FrameComposer.drawLayer(Canvas(it), size, size, cell, camera)
    }

    @Test
    fun drawSolid_fillsWholeFrame() {
        val bitmap = frame()
        FrameComposer.drawSolid(Canvas(bitmap), TEST_PALETTE.dark)
        assertEquals(0xFF333333.toInt(), bitmap.getPixel(5, 5))
        assertEquals(0xFF333333.toInt(), bitmap.getPixel(50, 50))
        assertEquals(0xFF333333.toInt(), bitmap.getPixel(94, 94))
    }

    @Test
    fun noBackground_leavesTransparent() {
        val bitmap = drawLayer(cell(100, 100, Square(0, 0, 20, BLACK)), CAMERA_ORIGIN)
        assertEquals(BLACK, bitmap.getPixel(10, 10))
        assertEquals(0, alpha(bitmap.getPixel(50, 50)))
    }

    @Test
    fun drawPhoto_landscapePhotoInSquare_cropsCenter() {
        val bitmap = frame()
        FrameComposer.drawPhoto(
            Canvas(bitmap),
            100,
            100,
            RenderTestImages.threeStripes(),
            TEST_PALETTE.white,
            CAMERA_ORIGIN,
        )
        // Stretched, (5, 50) would be GREEN; fitted with bars, (50, 5) would be white.
        assertEquals(RED, bitmap.getPixel(5, 50))
        assertEquals(RED, bitmap.getPixel(95, 50))
        assertEquals(RED, bitmap.getPixel(50, 5))
        assertEquals(RED, bitmap.getPixel(50, 94))
    }

    @Test
    fun drawPhoto_squarePhotoInPortrait_cropsCenter() {
        val bitmap = frame(width = 50, height = 100)
        val photo = RenderTestImages.verticalStripes(100, 50 to GREEN, 50 to RED)
        FrameComposer.drawPhoto(Canvas(bitmap), 50, 100, photo, TEST_PALETTE.white, CAMERA_ORIGIN)
        assertEquals(GREEN, bitmap.getPixel(10, 50))
        assertEquals(RED, bitmap.getPixel(40, 50))
    }

    @Test
    fun pan_movesLayerByFractionOfFrame() {
        val cell = cell(100, 100, Square(0, 0, 20, BLACK))
        assertEquals(BLACK, drawLayer(cell, CAMERA_ORIGIN).getPixel(10, 10))

        val panned = drawLayer(cell, key(panX = 0.5f, panY = 0.25f))
        assertEquals(BLACK, panned.getPixel(60, 35))
        assertEquals(0, alpha(panned.getPixel(10, 10)))
    }

    @Test
    fun zoom_scalesAroundCenter() {
        val cell = cell(100, 100, Square(40, 40, 20, BLACK))
        assertEquals(0, alpha(drawLayer(cell, key(zoom = 1f)).getPixel(33, 33)))
        // 40..60 becomes 30..70.
        assertEquals(BLACK, drawLayer(cell, key(zoom = 2f)).getPixel(33, 33))
    }

    @Test
    fun rotate_clockwiseAroundCenter() {
        val bitmap = drawLayer(cell(100, 100, Square(30, 30, 10, BLACK)), key(rotation = 90f))
        assertEquals(BLACK, bitmap.getPixel(65, 35))
        assertEquals(0, alpha(bitmap.getPixel(35, 35)))
    }

    @Test
    fun combined_scaleThenRotateThenPan() {
        val bitmap = drawLayer(cell(100, 100, Square(30, 30, 10, BLACK)), key(panX = 0.1f, zoom = 2f, rotation = 90f))
        // Zoom: 10..30 both ways; rotate: x 70..90, y 10..30; pan: x 80..100.
        assertEquals(BLACK, bitmap.getPixel(90, 20))
        assertEquals(0, alpha(bitmap.getPixel(70, 20)))
    }

    @Test
    fun pan_scalesWithOutputSize() {
        // The 100 × 100 cell is drawn into 200 × 200, so the square covers 0..40, then moves by 100.
        val bitmap = drawLayer(cell(100, 100, Square(0, 0, 20, BLACK)), key(panX = 0.5f), size = 200)
        assertEquals(BLACK, bitmap.getPixel(120, 20))
        assertEquals(0, alpha(bitmap.getPixel(20, 20)))
    }

    @Test
    fun drawPhoto_followsCamera_gapIsWhite() {
        val photo = cell(100, 100, Square(0, 0, 100, RED))
        val bitmap = frame()
        FrameComposer.drawPhoto(Canvas(bitmap), 100, 100, photo, TEST_PALETTE.white, key(zoom = 0.5f))
        assertEquals(TEST_PALETTE.white, bitmap.getPixel(5, 5))
        assertEquals(RED, bitmap.getPixel(50, 50))

        val solid = frame()
        FrameComposer.compose(
            Canvas(solid),
            100,
            100,
            FrameBackground.Solid(TEST_PALETTE.dark),
            emptyList(),
            key(zoom = 0.5f),
        )
        assertEquals(TEST_PALETTE.dark, solid.getPixel(5, 5))
    }
}
