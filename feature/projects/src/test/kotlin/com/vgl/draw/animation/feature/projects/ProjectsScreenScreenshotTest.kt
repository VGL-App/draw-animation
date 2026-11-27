package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.project.model.ProjectId
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Screenshot tests (Roborazzi). Images named like a design image (docs/features/projects/design/screens/) use the
 * English texts of the design and the four projects of its text table, with fake ads where the design has them
 * ([FakeProjectsAdSlots], plan T-010; tests have no ads controller), and no navigation bar (it belongs to :app, which
 * has its own AppNavigationBar images).
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")
class ProjectsScreenScreenshotTest {
    private val empty = ProjectsUiState.Empty(showLessonButton = true, showSettingsButton = true)

    @Test
    fun empty_light() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_empty_light.png") {
            Screen(empty, darkTheme = false, adSlots = FakeProjectsAdSlots())
        }
    }

    @Test
    fun empty_dark() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_empty_dark.png") {
            Screen(empty, darkTheme = true, adSlots = FakeProjectsAdSlots())
        }
    }

    @Test
    fun list_light() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_list_light.png") {
            Screen(
                ProjectsUiState.List(designCards(), showSettingsButton = true),
                darkTheme = false,
                adSlots = FakeProjectsAdSlots(),
            )
        }
    }

    @Test
    fun list_dark() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_list_dark.png") {
            Screen(
                ProjectsUiState.List(designCards(), showSettingsButton = true),
                darkTheme = true,
                adSlots = FakeProjectsAdSlots(),
            )
        }
    }

    @Test
    @Config(qualifiers = "en-w360dp-h640dp-xxhdpi")
    fun empty_small() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_empty_small.png") {
            Screen(empty, darkTheme = false, adSlots = FakeProjectsAdSlots())
        }
    }

    @Test
    @Config(qualifiers = "en-w360dp-h640dp-xxhdpi")
    fun list_small() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_list_small.png") {
            Screen(
                ProjectsUiState.List(eightCards(), showSettingsButton = true),
                darkTheme = false,
                adSlots = FakeProjectsAdSlots(),
            )
        }
    }

    /** No design frame has a grid ad: 8 projects, opened at the ad (item 8: button, header, projects 1 to 6). */
    @Test
    fun listGridAd_light() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_listGridAd_light.png") {
            Screen(
                ProjectsUiState.List(eightCards(), showSettingsButton = true),
                darkTheme = false,
                adSlots = FakeProjectsAdSlots(),
                firstVisibleItem = GRID_AD_ITEM,
            )
        }
    }

    @Test
    @Config(qualifiers = "en-w360dp-h640dp-xxhdpi")
    fun listGridAd_small() {
        captureRoboImage("src/test/screenshots/ProjectsScreen_listGridAd_small.png") {
            Screen(
                ProjectsUiState.List(eightCards(), showSettingsButton = true),
                darkTheme = false,
                adSlots = FakeProjectsAdSlots(),
                firstVisibleItem = GRID_AD_ITEM,
            )
        }
    }

    @Test
    fun list_unreadable() {
        val cards = designCards().toMutableList().apply {
            add(1, unreadableCard(9, name = "Old backup"))
            add(3, unreadableCard(10, name = null))
        }
        captureRoboImage("src/test/screenshots/ProjectsScreen_list_unreadable.png") {
            Screen(ProjectsUiState.List(cards, showSettingsButton = true), darkTheme = false)
        }
    }

    private fun eightCards(): List<ProjectCardUi> = designCards() + listOf(
        readableCard(5, "A very long project name that never fits on one card", frames = 120, fps = 24),
        readableCard(6, "Run cycle", frames = 12, fps = 12),
        readableCard(7, "Flip book", frames = 30, fps = 15),
        readableCard(8, "Title card", frames = 1, fps = 6),
    )

    @Composable
    private fun Screen(
        uiState: ProjectsUiState,
        darkTheme: Boolean,
        adSlots: ProjectsAdSlots = DefaultProjectsAdSlots,
        firstVisibleItem: Int = 0,
    ) {
        val cards = (uiState as? ProjectsUiState.List)?.cards.orEmpty()
        // Every id gets an image, also unreadable ones: the screen must not draw it for them.
        val thumbnails: Map<ProjectId, ImageBitmap?> = remember { cards.associate { it.id to fakeThumbnail() } }
        AppTheme(darkTheme = darkTheme) {
            ProjectsScreen(
                uiState = uiState,
                thumbnails = thumbnails,
                snackbarHostState = remember { SnackbarHostState() },
                onNewProject = {},
                onProjectClick = {},
                onOpenLessons = {},
                onOpenSettings = {},
                onImport = {},
                gridState = rememberLazyGridState(initialFirstVisibleItemIndex = firstVisibleItem),
                adSlots = adSlots,
            )
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

    private companion object {
        const val GRID_AD_ITEM = 8
    }
}
