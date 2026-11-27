package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.project.model.ProjectId
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Screenshot tests of the project menu and its dialogs (Roborazzi), named like the design images
 * `ProjectsScreen_menu_*`. The real sheet and dialogs open windows of their own, which the lambda capture does not
 * see, so each frame is drawn in one window: the list, a scrim at the design's 0.55, and the menu content (bottom) or
 * the dialog card (center), the very content the real sheet and dialogs wrap (plan T-012, section 3.4). The scrim
 * here only makes the image look like the design; it is no proof of the real sheet's scrim.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")
class ProjectMenuScreenshotTest {
    private val walk = readableCard(2, "Walk cycle", frames = 24, fps = 12).copy(updatedAtMillis = NOW - 60_000L)

    @Test
    fun menu_light() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_menu_light.png") {
            Frame(darkTheme = false, adSlots = FakeProjectsAdSlots()) { Menu(walk) }
        }
    }

    @Test
    fun menu_dark() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_menu_dark.png") {
            Frame(darkTheme = true, adSlots = FakeProjectsAdSlots()) { Menu(walk) }
        }
    }

    @Test
    @Config(qualifiers = "en-w360dp-h640dp-xxhdpi")
    fun menu_small() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_menu_small.png") {
            Frame(darkTheme = false) { Menu(walk.copy(name = "W".repeat(40))) }
        }
    }

    @Test
    fun menuUnreadable_light() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_menuUnreadable_light.png") {
            Frame(darkTheme = false) { Menu(unreadableCard(9, name = "Old test")) }
        }
    }

    @Test
    fun rename_light() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_rename_light.png") {
            Frame(darkTheme = false) { Rename() }
        }
    }

    @Test
    fun rename_dark() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_rename_dark.png") {
            Frame(darkTheme = true) { Rename() }
        }
    }

    @Test
    fun delete_light() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_delete_light.png") {
            Frame(darkTheme = false) { Delete() }
        }
    }

    @Test
    fun delete_dark() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_delete_dark.png") {
            Frame(darkTheme = true) { Delete() }
        }
    }

    @Test
    fun duplicating_light() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_duplicating_light.png") {
            Frame(darkTheme = false, menuState = ProjectsMenuState(duplicatingName = "Walk cycle"))
        }
    }

    /**
     * The list of the design's four projects; [overlay] is drawn over a scrim when not null. The design's menu frames
     * have the bottom bar under the scrim: they pass fake [adSlots] (plan T-010).
     */
    @Composable
    private fun Frame(
        darkTheme: Boolean,
        menuState: ProjectsMenuState = ProjectsMenuState(),
        adSlots: ProjectsAdSlots = DefaultProjectsAdSlots,
        overlay: (@Composable () -> Unit)? = null,
    ) {
        val cards = designCards()
        val thumbnails: Map<ProjectId, ImageBitmap?> = remember { cards.associate { it.id to fakeThumbnail() } }
        AppTheme(darkTheme = darkTheme) {
            Box(Modifier.fillMaxSize()) {
                ProjectsScreen(
                    uiState = ProjectsUiState.List(cards, showSettingsButton = true),
                    thumbnails = thumbnails,
                    snackbarHostState = remember { SnackbarHostState() },
                    onNewProject = {},
                    onProjectClick = {},
                    onOpenLessons = {},
                    onOpenSettings = {},
                    onImport = {},
                    menuState = menuState,
                    nowMillis = NOW,
                    zone = ZoneOffset.UTC,
                    adSlots = adSlots,
                )
                if (overlay != null) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = SCRIM_ALPHA)),
                    )
                    overlay()
                }
            }
        }
    }

    @Composable
    private fun Menu(card: ProjectCardUi) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
                ProjectMenuContent(
                    card = card,
                    thumbnail = if (card.readable) remember { solidThumbnail() } else null,
                    nowMillis = NOW,
                    zone = ZoneOffset.UTC,
                    duplicateEnabled = true,
                    bottomInset = 0.dp,
                    onOpen = {},
                    onRename = {},
                    onDuplicate = {},
                    onBackUp = {},
                    onDelete = {},
                )
            }
        }
    }

    @Composable
    private fun Rename() {
        Box(Modifier.fillMaxSize().padding(horizontal = DIALOG_MARGIN), contentAlignment = Alignment.Center) {
            RenameDialogCard(initialName = "Walk cycle", busy = false, onConfirm = {}, onDismiss = {})
        }
    }

    @Composable
    private fun Delete() {
        Box(Modifier.fillMaxSize().padding(horizontal = DIALOG_MARGIN), contentAlignment = Alignment.Center) {
            DeleteDialogCard(name = "Walk cycle", busy = false, onConfirm = {}, onDismiss = {})
        }
    }

    /** A 16:9 thumbnail like the design's: white paper, a ground line and a ball (context/ProjectsScreen_list_*). */
    private fun fakeThumbnail(): ImageBitmap {
        val image = ImageBitmap(336, 188)
        val canvas = Canvas(image)
        canvas.drawRect(Rect(0f, 0f, 336f, 188f), Paint().apply { color = Color.White })
        canvas.drawRect(Rect(34f, 146f, 302f, 150f), Paint().apply { color = Color(0xFF1A1C22) })
        canvas.drawCircle(Offset(205f, 127f), 21f, Paint().apply { color = Color(0xFF2F6FE0) })
        return image
    }

    /** The menu header's image in the design is plain paper: a solid white thumbnail. */
    private fun solidThumbnail(): ImageBitmap {
        val image = ImageBitmap(96, 96)
        Canvas(image).drawRect(Rect(0f, 0f, 96f, 96f), Paint().apply { color = Color.White })
        return image
    }

    private companion object {
        // 2026-11-22 12:00 UTC: the menu says "edited today".
        val NOW: Long = Instant.parse("2026-11-22T12:00:00Z").toEpochMilli()
        const val SCRIM_ALPHA = 0.55f
        val DIALOG_MARGIN = 24.dp
    }
}
