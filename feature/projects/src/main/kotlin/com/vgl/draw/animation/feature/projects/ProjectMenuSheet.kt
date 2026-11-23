package com.vgl.draw.animation.feature.projects

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import java.time.ZoneId

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
) = Unit

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
) = Unit
