package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.designsystem.theme.Spacing
import com.vgl.draw.animation.core.project.model.ProjectId

// Numbers of design/context/ProjectsScreen_list_*.md that have no Spacing token.
private val GridGap = 12.dp
private val SectionGap = 20.dp
private val CardCorner = 14.dp
private val CardBottomPadding = 12.dp
private val ThumbnailCorner = 8.dp
private const val THUMBNAIL_ASPECT = 168f / 94f

/** Columns of the grid, also given to the grid's ads. */
internal const val PROJECT_GRID_COLUMNS = 2

/**
 * The list state: "New animation" first, the "Your projects" header, then two columns of cards. The button and the
 * header span the whole row, so they scroll with the cards; the top bar stays outside the grid.
 */
@Composable
internal fun ProjectGrid(
    cards: List<ProjectCardUi>,
    thumbnails: Map<ProjectId, ImageBitmap?>,
    gridState: LazyGridState,
    onNewProject: () -> Unit,
    onProjectClick: (ProjectCardUi) -> Unit,
    onProjectLongClick: (ProjectCardUi) -> Unit,
    onThumbnailNeeded: (ProjectCardUi, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = modifier.fillMaxSize().testTag(ProjectsTestTags.GRID),
        contentPadding = PaddingValues(start = Spacing.md, end = Spacing.md, top = Spacing.sm, bottom = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(GridGap),
        verticalArrangement = Arrangement.spacedBy(GridGap),
    ) {
        item(key = "new", span = { GridItemSpan(maxLineSpan) }) {
            NewAnimationButton(onClick = onNewProject, modifier = Modifier.fillMaxWidth())
        }
        item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
            SectionHeader(Modifier.padding(vertical = SectionGap - GridGap))
        }
        items(cards, key = { it.id.value }) { card ->
            ProjectCard(
                card = card,
                thumbnail = if (card.readable) thumbnails[card.id] else null,
                onClick = { onProjectClick(card) },
                onLongClick = { onProjectLongClick(card) },
                onThumbnailNeeded = { width -> onThumbnailNeeded(card, width) },
            )
        }
    }
}

@Composable
private fun SectionHeader(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.projects_your_projects),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = stringResource(R.string.projects_recent),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * One project: thumbnail (whole picture, letterboxed on paper), name on one line cut with "…", frames and fps.
 * An unreadable card has no thumbnail and no frames line, but keeps the same height. A long press opens the menu.
 */
@Composable
internal fun ProjectCard(
    card: ProjectCardUi,
    thumbnail: ImageBitmap?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onThumbnailNeeded: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardCorner))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .combinedClickable(
                onLongClickLabel = stringResource(R.string.projects_card_long_click_label),
                onLongClick = onLongClick,
                onClick = onClick,
            )
            .testTag(ProjectsTestTags.card(card.id))
            .padding(start = Spacing.sm, top = Spacing.sm, end = Spacing.sm, bottom = CardBottomPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Thumbnail(card = card, image = thumbnail, onNeeded = onThumbnailNeeded)
        Text(
            text = card.name ?: stringResource(R.string.projects_card_unnamed),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = if (card.readable) {
                pluralStringResource(R.plurals.projects_card_frames, card.frameCount, card.frameCount, card.fps)
            } else {
                ""
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun Thumbnail(card: ProjectCardUi, image: ImageBitmap?, onNeeded: (Int) -> Unit) {
    val currentOnNeeded by rememberUpdatedState(onNeeded)
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(THUMBNAIL_ASPECT)
            .clip(RoundedCornerShape(ThumbnailCorner))
            .background(AppTheme.colors.canvas),
        contentAlignment = Alignment.Center,
    ) {
        val widthPx = constraints.maxWidth
        // Asks again when the project changes (new updatedAtMillis), so an edited project shows its new picture.
        LaunchedEffect(card.id, card.updatedAtMillis, widthPx) { currentOnNeeded(widthPx) }
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().testTag(ProjectsTestTags.thumbnail(card.id)),
            )
        }
    }
}
