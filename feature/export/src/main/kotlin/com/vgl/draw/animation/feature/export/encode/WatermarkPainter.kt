package com.vgl.draw.animation.feature.export.encode

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.vgl.draw.animation.core.designsystem.theme.WatermarkColors

class WatermarkPainter(
    val text: String,
    typeface: Typeface,
    icon: Drawable,
    contentArgb: Int = WatermarkColors.CONTENT,
    pillArgb: Int = WatermarkColors.PILL,
) {
    fun pillBounds(width: Int, height: Int): RectF = TODO()

    fun draw(canvas: Canvas, width: Int, height: Int, opaquePill: Boolean = false): Unit = TODO()

    companion object {
        fun create(context: Context): WatermarkPainter = TODO()
    }
}
