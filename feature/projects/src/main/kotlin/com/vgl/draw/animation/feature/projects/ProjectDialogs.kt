package com.vgl.draw.animation.feature.projects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

@Composable
internal fun RenameProjectDialog(
    initialName: String,
    busy: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) = Unit

@Composable
internal fun DeleteProjectDialog(name: String, busy: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) = Unit

@Composable
internal fun RenameDialogCard(
    initialName: String,
    busy: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
) = Unit

@Composable
internal fun DeleteDialogCard(
    name: String,
    busy: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) = Unit
