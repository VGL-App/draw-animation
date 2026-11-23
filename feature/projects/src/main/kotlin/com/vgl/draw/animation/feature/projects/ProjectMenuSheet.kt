package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.designsystem.theme.Spacing
import java.time.ZoneId

// Numbers of design/context/ProjectsScreen_menu_*.md that have no Spacing token.
private val HandleBand = 24.dp
private val HandleWidth = 32.dp
private val HandleHeight = 4.dp
private val HandleCorner = 2.dp
private val HeaderHeight = 56.dp
private val HeaderImage = 48.dp
private val HeaderImageCorner = 8.dp
private val HeaderGap = 12.dp
private val ItemHeight = 56.dp

/** Opacity of the scrim behind the menu, from the design. */
private const val MENU_SCRIM_ALPHA = 0.55f

/**
 * The menu of one project (spec projects, KB-4), without a window: handle, header (thumbnail, name, "<n> frames ·
 * <fps> fps · edited <when>") and the items. An unreadable project has only "Delete" and no meta line (KB-2). The
 * bottom padding is 16 dp plus [bottomInset], the system bar height read by the screen (the sheet adds no inset).
 * No ad here (spec section 5).
 */
@Composable
internal fun ProjectMenuContent(
    card: ProjectCardUi,
    thumbnail: ImageBitmap?,
    nowMillis: Long,
    zone: ZoneId,
    duplicateEnabled: Boolean,
    bottomInset: Dp,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onBackUp: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(ProjectsTestTags.MENU)
            .padding(bottom = Spacing.md + bottomInset),
    ) {
        Box(Modifier.fillMaxWidth().height(HandleBand), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(width = HandleWidth, height = HandleHeight)
                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(HandleCorner)),
            )
        }
        MenuHeader(card = card, thumbnail = thumbnail, nowMillis = nowMillis, zone = zone)
        if (card.readable) {
            MenuItem(
                label = stringResource(R.string.projects_menu_open),
                icon = rememberVectorPainter(Icons.Rounded.Edit),
                tag = ProjectsTestTags.MENU_OPEN,
                onClick = onOpen,
            )
            MenuItem(
                label = stringResource(R.string.projects_menu_rename),
                icon = painterResource(R.drawable.ic_menu_rename),
                tag = ProjectsTestTags.MENU_RENAME,
                onClick = onRename,
            )
            MenuItem(
                label = stringResource(R.string.projects_menu_duplicate),
                icon = painterResource(R.drawable.ic_menu_duplicate),
                tag = ProjectsTestTags.MENU_DUPLICATE,
                enabled = duplicateEnabled,
                onClick = onDuplicate,
            )
            MenuItem(
                label = stringResource(R.string.projects_menu_backup),
                icon = painterResource(R.drawable.ic_menu_backup),
                tag = ProjectsTestTags.MENU_BACKUP,
                trailing = stringResource(R.string.projects_menu_backup_extension),
                onClick = onBackUp,
            )
        }
        MenuItem(
            label = stringResource(R.string.projects_menu_delete),
            icon = rememberVectorPainter(Icons.Rounded.Delete),
            tag = ProjectsTestTags.MENU_DELETE,
            color = MaterialTheme.colorScheme.error,
            onClick = onDelete,
        )
    }
}

/**
 * [ProjectMenuContent] in a Material 3 bottom sheet: fully expanded at once, square corners and no own handle (both as
 * the design's frame), the design's scrim, no window inset (the content adds the system bar height once).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProjectMenuSheet(
    card: ProjectCardUi,
    thumbnail: ImageBitmap?,
    nowMillis: Long,
    zone: ZoneId,
    duplicateEnabled: Boolean,
    bottomInset: Dp,
    onDismissRequest: () -> Unit,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onBackUp: () -> Unit,
    onDelete: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RectangleShape,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = MENU_SCRIM_ALPHA),
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) },
    ) {
        ProjectMenuContent(
            card = card,
            thumbnail = thumbnail,
            nowMillis = nowMillis,
            zone = zone,
            duplicateEnabled = duplicateEnabled,
            bottomInset = bottomInset,
            onOpen = onOpen,
            onRename = onRename,
            onDuplicate = onDuplicate,
            onBackUp = onBackUp,
            onDelete = onDelete,
        )
    }
}

@Composable
private fun MenuHeader(card: ProjectCardUi, thumbnail: ImageBitmap?, nowMillis: Long, zone: ZoneId) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .padding(start = Spacing.lg, end = Spacing.lg, bottom = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HeaderGap),
    ) {
        Box(
            Modifier
                .size(HeaderImage)
                .clip(RoundedCornerShape(HeaderImageCorner))
                .background(AppTheme.colors.canvas),
        ) {
            if (card.readable && thumbnail != null) {
                Image(
                    bitmap = thumbnail,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = card.name ?: stringResource(R.string.projects_card_unnamed),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (card.readable) {
                val frames = pluralStringResource(
                    R.plurals.projects_card_frames,
                    card.frameCount,
                    card.frameCount,
                    card.fps,
                )
                val edited = editedLabel(editedAt(card.updatedAtMillis, nowMillis, zone))
                Text(
                    text = stringResource(R.string.projects_menu_meta, frames, edited),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    label: String,
    icon: Painter,
    tag: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    trailing: String? = null,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    // ListItem has no enabled state of its own: a disabled item is dimmed here.
    val contentColor = if (enabled) color else color.copy(alpha = DISABLED_ALPHA)
    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = { Icon(painter = icon, contentDescription = null) },
        trailingContent = trailing?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            headlineColor = contentColor,
            leadingIconColor = contentColor,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(ItemHeight)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .testTag(tag),
    )
}

// Material's opacity for disabled content.
private const val DISABLED_ALPHA = 0.38f
