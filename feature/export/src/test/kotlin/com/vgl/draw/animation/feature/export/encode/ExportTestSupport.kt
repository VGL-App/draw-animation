package com.vgl.draw.animation.feature.export.encode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.Frame
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.render.FrameRenderer
import com.vgl.draw.animation.core.project.render.RenderSize
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.imageio.metadata.IIOMetadataNode

const val RED: Int = 0xFFFF0000.toInt()
const val GREEN: Int = 0xFF00FF00.toInt()
const val BLUE: Int = 0xFF0000FF.toInt()
const val YELLOW: Int = 0xFFFFFF00.toInt()
const val WHITE: Int = 0xFFFFFFFF.toInt()

val FRAME_COLORS: List<Int> = listOf(RED, GREEN, BLUE, YELLOW)

/** A project with one frame per entry of [holds]; its name must never reach a log. */
fun exportTestProject(
    holds: List<Int>,
    fps: Int = 12,
    aspectRatio: AspectRatio = AspectRatio.SQUARE_1_1,
    resolution: Resolution = Resolution.P720,
): Project = Project.create(
    id = ProjectId.random(),
    name = "Secret name",
    aspectRatio = aspectRatio,
    resolution = resolution,
    frameRate = requireNotNull(FrameRate.fromFpsOrNull(fps)),
    background = Background.Solid(BackgroundColor.WHITE),
    firstLayerName = "Layer 1",
    nowMillis = 0L,
).copy(frames = holds.map { Frame(FrameId.random(), hold = it) })

/** One call of [FakeFrameRenderer.renderInto]. */
data class RenderCall(
    val project: Project,
    val frameIndex: Int,
    val includeBackground: Boolean,
    val width: Int,
    val height: Int,
)

/**
 * Fills frame k with `FRAME_COLORS[k % 4]` when the background is included; otherwise clears to transparent and fills
 * the square from (w / 4, h / 4) to (w / 2, h / 2). Throws [IOException] at frame [failAt].
 */
class FakeFrameRenderer(private val failAt: Int? = null) : FrameRenderer {
    private val recorded = mutableListOf<RenderCall>()

    val calls: List<RenderCall> get() = synchronized(recorded) { recorded.toList() }

    override suspend fun render(
        project: Project,
        frameIndex: Int,
        size: RenderSize,
        includeBackground: Boolean,
    ): Bitmap {
        val target = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        renderInto(target, project, frameIndex, includeBackground)
        return target
    }

    override suspend fun renderInto(target: Bitmap, project: Project, frameIndex: Int, includeBackground: Boolean) {
        synchronized(recorded) {
            recorded += RenderCall(project, frameIndex, includeBackground, target.width, target.height)
        }
        if (frameIndex == failAt) throw IOException("disk full")
        val color = FRAME_COLORS[frameIndex % FRAME_COLORS.size]
        if (includeBackground) {
            target.eraseColor(color)
        } else {
            target.eraseColor(Color.TRANSPARENT)
            val w = target.width.toFloat()
            val h = target.height.toFloat()
            val paint = Paint().apply {
                isAntiAlias = false
                this.color = color
            }
            Canvas(target).drawRect(w / 4, h / 4, w / 2, h / 2, paint)
        }
    }
}

/** A point inside the pill, above the icon, away from the text and the rounded ends. */
fun pillPoint(bounds: RectF): Pair<Int, Int> =
    (bounds.left + bounds.height() / 2).toInt() to (bounds.top + bounds.height() / 10).toInt()

/** One decoded GIF frame and its graphic control extension. */
class GifTestFrame(
    val image: BufferedImage,
    val delayTime: Int,
    val disposalMethod: String,
    val transparentColorFlag: Boolean,
) {
    fun argb(x: Int, y: Int): Int = image.getRGB(x, y)
}

class GifTestFile(val frames: List<GifTestFrame>, val loopCount: Int?)

/** Reads a GIF with the JDK's own decoder, independent of [GifEncoder]. */
object GifTestReader {
    private const val FORMAT = "javax_imageio_gif_image_1.0"

    fun read(bytes: ByteArray): GifTestFile {
        val reader = ImageIO.getImageReadersByFormatName("gif").next()
        try {
            ImageIO.createImageInputStream(ByteArrayInputStream(bytes)).use { input ->
                reader.setInput(input, false)
                val count = reader.getNumImages(true)
                var loopCount: Int? = null
                val frames = (0 until count).map { index ->
                    val tree = reader.getImageMetadata(index).getAsTree(FORMAT) as IIOMetadataNode
                    val control = tree.getElementsByTagName("GraphicControlExtension").item(0) as IIOMetadataNode
                    if (index == 0) loopCount = netscapeLoopCount(tree)
                    GifTestFrame(
                        image = reader.read(index),
                        delayTime = control.getAttribute("delayTime").toInt(),
                        disposalMethod = control.getAttribute("disposalMethod"),
                        transparentColorFlag = control.getAttribute("transparentColorFlag") == "TRUE",
                    )
                }
                return GifTestFile(frames, loopCount)
            }
        } finally {
            reader.dispose()
        }
    }

    private fun netscapeLoopCount(tree: IIOMetadataNode): Int? {
        val extensions = tree.getElementsByTagName("ApplicationExtension")
        for (i in 0 until extensions.length) {
            val node = extensions.item(i) as IIOMetadataNode
            if (node.getAttribute("applicationID") == "NETSCAPE") {
                val data = node.userObject as ByteArray
                return (data[1].toInt() and 0xFF) or ((data[2].toInt() and 0xFF) shl 8)
            }
        }
        return null
    }
}

fun alpha(argb: Int): Int = argb ushr 24

fun red(argb: Int): Int = (argb shr 16) and 0xFF

fun green(argb: Int): Int = (argb shr 8) and 0xFF

fun blue(argb: Int): Int = argb and 0xFF
