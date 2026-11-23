package com.vgl.draw.animation.feature.projects

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.analytics.Analytics
import com.vgl.draw.animation.core.analytics.AnalyticsEvent
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.config.FeatureFlags
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.Frame
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import com.vgl.draw.animation.feature.projects.data.ProjectsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A [ProjectsRepository] in memory. [projects] is what `items` emits; [images] holds the thumbnail of each id;
 * [reads] counts the thumbnail reads per id; [gate], when set, holds every read until it completes.
 * [readGates] holds one read: key (id, n) is the n-th read of that id, from 1. A read returns the image [images] held
 * when the read started, as the real repository reads the file of the version asked for.
 */
class FakeProjectsRepository : ProjectsRepository {
    val projects = MutableStateFlow<List<ProjectListItem>>(emptyList())
    val images = mutableMapOf<ProjectId, ImageBitmap>()
    val reads = mutableMapOf<ProjectId, Int>()
    var gate: CompletableDeferred<Unit>? = null
    val readGates = mutableMapOf<Pair<ProjectId, Int>, CompletableDeferred<Unit>>()

    /** How many collectors `items` has right now. */
    val collectors: Int get() = projects.subscriptionCount.value

    override val items: Flow<List<ProjectListItem>> = projects

    override suspend fun thumbnail(id: ProjectId, maxWidthPx: Int): ImageBitmap? {
        val n = (reads[id] ?: 0) + 1
        reads[id] = n
        val image = images[id]
        gate?.await()
        readGates[id to n]?.await()
        return image
    }

    fun readsOf(id: ProjectId): Int = reads[id] ?: 0
}

/** Every flag starts on; tests turn single flags off. */
class FakeFeatureFlags(vararg off: FeatureFlag) : FeatureFlags {
    val values: Map<FeatureFlag, MutableStateFlow<Boolean>> =
        FeatureFlag.entries.associateWith { MutableStateFlow(it !in off) }

    operator fun set(flag: FeatureFlag, enabled: Boolean) {
        values.getValue(flag).value = enabled
    }

    override fun enabled(flag: FeatureFlag): Flow<Boolean> = values.getValue(flag)
}

class NoAnalytics : Analytics {
    val events = mutableListOf<AnalyticsEvent>()

    override fun log(event: AnalyticsEvent) {
        events += event
    }
}

/** A fixed project id; [n] from 1. */
fun testProjectId(n: Int): ProjectId = ProjectId("00000000-0000-4000-8000-%012d".format(n))

/** A readable project with [frames] frames at [fps]. */
fun readableItem(
    n: Int,
    name: String = "Project $n",
    frames: Int = 1,
    fps: FrameRate = FrameRate.FPS_12,
    updatedAtMillis: Long = 1_000L + n,
): ProjectListItem.Readable {
    val project = Project.create(
        id = testProjectId(n),
        name = name,
        aspectRatio = AspectRatio.PORTRAIT_9_16,
        resolution = Resolution.P720,
        frameRate = fps,
        background = Background.Solid(BackgroundColor.WHITE),
        firstLayerName = "Layer 1",
        nowMillis = updatedAtMillis,
    )
    return ProjectListItem.Readable(project.copy(frames = List(frames) { Frame(FrameId.random()) }))
}

fun unreadableItem(n: Int, name: String? = null): ProjectListItem.Unreadable =
    ProjectListItem.Unreadable(id = testProjectId(n), name = name, newerVersion = false)

/** The card of a readable project, as the screen tests pass it. */
fun readableCard(n: Int, name: String = "Project $n", frames: Int = 1, fps: Int = 12): ProjectCardUi = ProjectCardUi(
    id = testProjectId(n),
    name = name,
    frameCount = frames,
    fps = fps,
    updatedAtMillis = 1_000L + n,
    readable = true,
)

fun unreadableCard(n: Int, name: String? = null): ProjectCardUi = ProjectCardUi(
    id = testProjectId(n),
    name = name,
    frameCount = 0,
    fps = 0,
    updatedAtMillis = 0L,
    readable = false,
)

/** The four projects of the design's text table (design/context/ProjectsScreen_list_*.md). */
fun designCards(): List<ProjectCardUi> = listOf(
    readableCard(1, "Bouncing ball", frames = 48, fps = 12),
    readableCard(2, "Walk cycle", frames = 24, fps = 12),
    readableCard(3, "Logo sting", frames = 90, fps = 24),
    readableCard(4, "Blink test", frames = 16, fps = 8),
)
