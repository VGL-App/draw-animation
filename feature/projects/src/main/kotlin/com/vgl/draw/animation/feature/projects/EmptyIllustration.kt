package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.vgl.draw.animation.core.designsystem.theme.AppTheme

// Sizes of design/context/ProjectsScreen_empty_*.md, frame "Illustration": 160 x 120, corner 14, three dots of 40.
private val IllustrationWidth = 160.dp
private val IllustrationHeight = 120.dp
private val IllustrationCorner = 14.dp
private val DotRadius = 20.dp

// Dot centers inside the frame, in dp: a ball moving up and to the right.
private val DotCenters = listOf(50 to 90, 90 to 74, 130 to 58)

/**
 * The picture of the empty project list: three dots on paper. The ground line of the design lies below the frame
 * (y 130 of 120) and is clipped there, so it is not drawn.
 */
@Composable
internal fun EmptyIllustration(modifier: Modifier = Modifier) {
    val dotColor = MaterialTheme.colorScheme.secondary
    Box(
        modifier = modifier
            .size(IllustrationWidth, IllustrationHeight)
            .clip(RoundedCornerShape(IllustrationCorner))
            .background(AppTheme.colors.canvas),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            DotCenters.forEach { (x, y) ->
                drawCircle(color = dotColor, radius = DotRadius.toPx(), center = Offset(x.dp.toPx(), y.dp.toPx()))
            }
        }
    }
}
