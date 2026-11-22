package com.vgl.draw.animation.feature.export.encode

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.vgl.draw.animation.core.designsystem.R as DesignR
import com.vgl.draw.animation.core.designsystem.theme.WatermarkColors
import com.vgl.draw.animation.feature.export.R
import kotlin.math.roundToInt

/**
 * Draws the Frameloop watermark: [icon] and [text] in [contentArgb] on a pill of [pillArgb], at the bottom right.
 * The pill is 5% of the frame's short edge tall and 3% of it away from the right and bottom edges; icon and text are
 * half the pill's height, with a quarter of it as padding and gap (export design, `PreviewWatermark`).
 *
 * Holds no state that changes between draws: every call makes its own [Paint] and copy of [icon], so the Export
 * screen's preview and an export running on another thread can share one instance.
 */
class WatermarkPainter(
    val text: String,
    private val typeface: Typeface,
    private val icon: Drawable,
    private val contentArgb: Int = WatermarkColors.CONTENT,
    private val pillArgb: Int = WatermarkColors.PILL,
) {
    /** Bounds of the pill on a frame of [width] x [height]. */
    fun pillBounds(width: Int, height: Int): RectF {
        val h = pillHeight(width, height).toFloat()
        val margin = margin(width, height).toFloat()
        val pillWidth = h / 4 + h / 2 + h / 4 + textPaint(h).measureText(text) + h / 4
        val right = width - margin
        val bottom = height - margin
        return RectF(right - pillWidth, bottom - h, right, bottom)
    }

    /** Draws the watermark; [opaquePill] draws the pill without transparency, for 1-bit transparent GIF. */
    fun draw(canvas: Canvas, width: Int, height: Int, opaquePill: Boolean = false) {
        val bounds = pillBounds(width, height)
        val h = bounds.height()
        val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (opaquePill) pillArgb or OPAQUE else pillArgb
        }
        canvas.drawRoundRect(bounds, h / 2, h / 2, pillPaint)

        val iconLeft = bounds.left + h / 4
        val iconTop = bounds.centerY() - h / 4
        val iconSize = h / 2
        icon.constantState!!.newDrawable().mutate().apply {
            setBounds(
                iconLeft.roundToInt(),
                iconTop.roundToInt(),
                (iconLeft + iconSize).roundToInt(),
                (iconTop + iconSize).roundToInt(),
            )
            setTint(contentArgb)
            draw(canvas)
        }

        val paint = textPaint(h)
        val baseline = bounds.centerY() - (paint.ascent() + paint.descent()) / 2
        canvas.drawText(text, iconLeft + iconSize + h / 4, baseline, paint)
    }

    private fun textPaint(pillHeight: Float): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = this@WatermarkPainter.typeface
        textSize = pillHeight / 2
        color = contentArgb
    }

    companion object {
        private const val OPAQUE = 0xFF000000.toInt()

        // round(5% and 3% of the short edge), in integers so 1080 gives exactly 54 and 32.
        private fun pillHeight(width: Int, height: Int): Int = (minOf(width, height) * 5 + 50) / 100

        private fun margin(width: Int, height: Int): Int = (minOf(width, height) * 3 + 50) / 100

        /** The watermark of the app: brand name, Inter Semi Bold and the brand mark of the design system. */
        fun create(context: Context): WatermarkPainter = WatermarkPainter(
            text = context.getString(R.string.export_watermark_brand),
            typeface = context.resources.getFont(DesignR.font.inter_semibold),
            icon = requireNotNull(context.getDrawable(DesignR.drawable.brand_mark)) { "brand_mark is missing" },
        )
    }
}
