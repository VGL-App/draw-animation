package com.vgl.draw.animation.feature.projects.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vgl.draw.animation.core.designsystem.theme.ProjectBackgroundColors
import com.vgl.draw.animation.core.designsystem.theme.Spacing
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.feature.projects.R

// Numbers of design/context/NewProjectScreen_default_*.md that have no Spacing token.
private val TileHeight = 84.dp
private val TileCorner = 14.dp
private val TileTopPadding = 12.dp
private val TileBottomPadding = 10.dp
private val SelectedBorder = 2.dp
private val ShapeStroke = 2.dp
private val ShapeCorner = 3.dp
private val ChipHeight = 28.dp
private val ChipCorner = 8.dp
private val ChipPadding = 12.dp
private val SwatchSize = 36.dp
private val SwatchSelected = 28.dp
private val SwatchUnselected = 32.dp
private val SwatchBorder = 1.dp
private val RingStroke = 2.dp
private val PhotoButtonSize = 48.dp
private val PhotoRing = 2.dp
private val PhotoLabelGap = 12.dp
private const val DISABLED_ALPHA = 0.38f

/** One tile of the "Canvas" group: a drawing of the shape and its ratio. */
@Composable
internal fun RatioTile(
    aspectRatio: AspectRatio,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(TileCorner)
    val content = if (selected) colors.onPrimaryContainer else colors.onSurface
    Column(
        modifier = modifier
            .height(TileHeight)
            .clip(shape)
            .background(if (selected) colors.primaryContainer else colors.surfaceContainer, shape)
            .then(if (selected) Modifier.border(BorderStroke(SelectedBorder, colors.primary), shape) else Modifier)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .testTag(NewProjectTags.ratio(aspectRatio))
            .padding(top = TileTopPadding, bottom = TileBottomPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.Bottom),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val (width, height) = shapeSize(aspectRatio)
        Box(
            Modifier
                .size(width, height)
                .border(
                    ShapeStroke,
                    if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
                    RoundedCornerShape(ShapeCorner),
                ),
        )
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = content)
    }
}

private fun shapeSize(aspectRatio: AspectRatio): Pair<Dp, Dp> = when (aspectRatio) {
    AspectRatio.PORTRAIT_9_16 -> 18.dp to 32.dp
    AspectRatio.LANDSCAPE_16_9 -> 32.dp to 18.dp
    AspectRatio.SQUARE_1_1 -> 26.dp to 26.dp
}

/** A chip of the "Size" and "Frame rate" groups. */
@Composable
internal fun OptionChip(label: String, selected: Boolean, onClick: () -> Unit, tag: String) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(ChipCorner)
    Box(
        modifier = Modifier
            .height(ChipHeight)
            .clip(shape)
            .background(if (selected) colors.primaryContainer else colors.surfaceVariant, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .testTag(tag)
            .padding(horizontal = ChipPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) colors.onPrimaryContainer else colors.onSurface,
        )
    }
}

/** A color of the "Background" group: the fixed project color, the same in both themes. */
@Composable
internal fun ColorSwatch(color: BackgroundColor, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val description = stringResource(
        when (color) {
            BackgroundColor.WHITE -> R.string.new_project_color_white
            BackgroundColor.DARK -> R.string.new_project_color_dark
            BackgroundColor.BLUE -> R.string.new_project_color_blue
        },
    )
    Box(
        modifier = Modifier
            .size(SwatchSize)
            .clip(CircleShape)
            .then(if (selected) Modifier.border(RingStroke, colors.onSurface, CircleShape) else Modifier)
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = description }
            .testTag(NewProjectTags.background(color)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(if (selected) SwatchSelected else SwatchUnselected)
                .background(Color(projectColor(color)), CircleShape)
                .border(SwatchBorder, colors.outlineVariant, CircleShape),
        )
    }
}

private fun projectColor(color: BackgroundColor): Int = when (color) {
    BackgroundColor.WHITE -> ProjectBackgroundColors.WHITE
    BackgroundColor.DARK -> ProjectBackgroundColors.DARK
    BackgroundColor.BLUE -> ProjectBackgroundColors.BLUE
}

/** The "Photo" choice: a round button (the photo's preview once there is one) and its label. */
@Composable
internal fun PhotoButton(preview: ImageBitmap?, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .testTag(NewProjectTags.BG_PHOTO)
            .alpha(if (enabled) 1f else DISABLED_ALPHA),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PhotoLabelGap),
    ) {
        Box(
            modifier = Modifier
                .size(PhotoButtonSize)
                .clip(CircleShape)
                .background(colors.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (preview != null) {
                Image(
                    bitmap = preview,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(PhotoButtonSize).clip(CircleShape),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_image),
                    contentDescription = null,
                    tint = colors.onSurface,
                )
            }
            if (selected) Box(Modifier.size(PhotoButtonSize).border(PhotoRing, colors.onSurface, CircleShape))
        }
        Text(
            text = stringResource(R.string.new_project_photo),
            style = MaterialTheme.typography.labelLarge,
            color = colors.onSurfaceVariant,
        )
    }
}
