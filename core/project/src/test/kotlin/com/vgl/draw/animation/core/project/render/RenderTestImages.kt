package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.Frame
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Layer
import com.vgl.draw.animation.core.project.model.LayerId
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution
import java.io.ByteArrayOutputStream

const val BLACK: Int = 0xFF000000.toInt()
const val RED: Int = 0xFFFF0000.toInt()
const val GREEN: Int = 0xFF00FF00.toInt()
const val BLUE_INK: Int = 0xFF0000FF.toInt()

/** Differs from [BackgroundPalette.Default], to show that colors follow the palette given to the renderer. */
val TEST_PALETTE = BackgroundPalette(white = 0xFFFFFFFF.toInt(), dark = 0xFF333333.toInt(), blue = 0xFF0055AA.toInt())

/** A filled square of [size] pixels with its top left corner at ([left], [top]). */
data class Square(val left: Int, val top: Int, val size: Int, val color: Int)

/** Test images and projects for the renderer tests. */
object RenderTestImages {
    /** A transparent [width] × [height] image with [squares] drawn in solid color, without anti-aliasing. */
    fun cell(width: Int, height: Int, vararg squares: Square): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        squares.forEach { square ->
            val paint = Paint().apply {
                isAntiAlias = false
                color = square.color
            }
            canvas.drawRect(
                square.left.toFloat(),
                square.top.toFloat(),
                (square.left + square.size).toFloat(),
                (square.top + square.size).toFloat(),
                paint,
            )
        }
        return bitmap
    }

    /** An image of vertical stripes, left to right; each stripe is `width to color`. */
    fun verticalStripes(height: Int, vararg stripes: Pair<Int, Int>): Bitmap {
        val bitmap = Bitmap.createBitmap(stripes.sumOf { it.first }, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        var left = 0
        stripes.forEach { (width, color) ->
            val paint = Paint().apply {
                isAntiAlias = false
                this.color = color
            }
            canvas.drawRect(left.toFloat(), 0f, (left + width).toFloat(), height.toFloat(), paint)
            left += width
        }
        return bitmap
    }

    /** 300 × 100: `GREEN`, `RED`, `BLUE_INK` stripes 100 wide each. */
    fun threeStripes(): Bitmap = verticalStripes(100, 100 to GREEN, 100 to RED, 100 to BLUE_INK)

    fun png(bitmap: Bitmap): ByteArray = ByteArrayOutputStream().also {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }.toByteArray()

    fun decode(bytes: ByteArray): Bitmap = checkNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))

    fun alpha(pixel: Int): Int = Color.alpha(pixel)

    fun pixels(bitmap: Bitmap): IntArray = IntArray(bitmap.width * bitmap.height).also {
        bitmap.getPixels(it, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    }

    /** A project with [layerCount] visible layers and [frameCount] frames, camera off, no reference, onion skin off. */
    fun project(
        aspectRatio: AspectRatio = AspectRatio.SQUARE_1_1,
        resolution: Resolution = Resolution.P720,
        background: Background = Background.Solid(BackgroundColor.WHITE),
        layerCount: Int = 1,
        frameCount: Int = 1,
    ): Project = Project.create(
        id = ProjectId.random(),
        name = "Render test",
        aspectRatio = aspectRatio,
        resolution = resolution,
        frameRate = FrameRate.FPS_12,
        background = background,
        firstLayerName = "Layer 1",
        nowMillis = 1_763_769_600_000,
    ).copy(
        layers = List(layerCount) { Layer(LayerId.random(), "Layer ${it + 1}", visible = true) },
        frames = List(frameCount) { Frame(FrameId.random()) },
    )
}
