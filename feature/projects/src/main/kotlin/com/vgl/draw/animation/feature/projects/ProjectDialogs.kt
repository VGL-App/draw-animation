package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vgl.draw.animation.core.designsystem.theme.Spacing
import com.vgl.draw.animation.core.project.repository.ProjectNames

// Sizes of the Material 3 basic dialog.
private val DialogMinWidth = 280.dp
private val DialogMaxWidth = 560.dp
private val TitleGap = 16.dp

/**
 * The rename dialog (spec projects, KB-5): a Material 3 dialog window around [RenameDialogCard]. The field gets the
 * focus when the dialog opens, so the soft keyboard shows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RenameProjectDialog(
    initialName: String,
    busy: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        val focusRequester = remember { FocusRequester() }
        RenameDialogCard(
            initialName = initialName,
            busy = busy,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            focusRequester = focusRequester,
        )
        LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
    }
}

/** The delete confirmation (spec projects, KB-7): a Material 3 dialog window around [DeleteDialogCard]. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeleteProjectDialog(name: String, busy: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        DeleteDialogCard(name = name, busy = busy, onConfirm = onConfirm, onDismiss = onDismiss)
    }
}

/**
 * The body of the rename dialog, without a window: title, one-line field holding the current name with the cursor at
 * the end, "Cancel" and "Save". The text is cut at [ProjectNames.MAX_NAME_LENGTH] code points as it is typed and
 * survives a configuration change. "Save" is off for a blank text and while the name is written.
 */
@Composable
internal fun RenameDialogCard(
    initialName: String,
    busy: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
) {
    var value by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialName, TextRange(initialName.length)))
    }
    val canSave = value.text.isNotBlank() && !busy
    DialogCard(modifier.testTag(ProjectsTestTags.RENAME_DIALOG)) {
        DialogTitle(stringResource(R.string.projects_rename_title), maxLines = 1)
        OutlinedTextField(
            value = value,
            onValueChange = { value = cutToMaxLength(it) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (canSave) onConfirm(value.text) }),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .testTag(ProjectsTestTags.RENAME_FIELD),
        )
        DialogButtons {
            DialogTextButton(
                text = stringResource(R.string.projects_rename_cancel),
                color = MaterialTheme.colorScheme.primary,
                tag = ProjectsTestTags.RENAME_CANCEL,
                onClick = onDismiss,
            )
            DialogTextButton(
                text = stringResource(R.string.projects_rename_save),
                color = MaterialTheme.colorScheme.primary,
                tag = ProjectsTestTags.RENAME_SAVE,
                enabled = canSave,
                onClick = { onConfirm(value.text) },
            )
        }
    }
}

/** The body of the delete confirmation, without a window: "Delete “<name>”?", "This can't be undone.", buttons. */
@Composable
internal fun DeleteDialogCard(
    name: String,
    busy: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DialogCard(modifier.testTag(ProjectsTestTags.DELETE_DIALOG)) {
        DialogTitle(stringResource(R.string.projects_delete_title, name), maxLines = 2)
        Text(
            text = stringResource(R.string.projects_delete_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DialogButtons {
            DialogTextButton(
                text = stringResource(R.string.projects_delete_cancel),
                color = MaterialTheme.colorScheme.primary,
                tag = ProjectsTestTags.DELETE_CANCEL,
                enabled = !busy,
                onClick = onDismiss,
            )
            DialogTextButton(
                text = stringResource(R.string.projects_delete_confirm),
                color = MaterialTheme.colorScheme.error,
                tag = ProjectsTestTags.DELETE_CONFIRM,
                enabled = !busy,
                onClick = onConfirm,
            )
        }
    }
}

/** Keeps at most [ProjectNames.MAX_NAME_LENGTH] code points, never half of a surrogate pair. */
internal fun cutToMaxLength(value: TextFieldValue): TextFieldValue {
    val text = value.text
    if (text.codePointCount(0, text.length) <= ProjectNames.MAX_NAME_LENGTH) return value
    val cut = text.substring(0, text.offsetByCodePoints(0, ProjectNames.MAX_NAME_LENGTH))
    val selection =
        TextRange(value.selection.start.coerceAtMost(cut.length), value.selection.end.coerceAtMost(cut.length))
    return value.copy(text = cut, selection = selection)
}

// The container of a Material 3 basic dialog: large corners, surfaceContainerHigh, 24 dp padding.
@Composable
private fun DialogCard(modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier.sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(Modifier.padding(Spacing.lg), content = content)
    }
}

@Composable
private fun DialogTitle(text: String, maxLines: Int) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
    Spacer(Modifier.height(TitleGap))
}

@Composable
private fun DialogButtons(content: @Composable () -> Unit) {
    Spacer(Modifier.height(Spacing.lg))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.End),
    ) { content() }
}

// Text buttons on the dialog's surface take the colors given here, not the default primary (CLAUDE.md, step 10).
@Composable
private fun DialogTextButton(text: String, color: Color, tag: String, onClick: () -> Unit, enabled: Boolean = true) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(contentColor = color),
        modifier = Modifier.testTag(tag),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
