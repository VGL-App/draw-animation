package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vgl.draw.animation.core.ads.AdPlacement
import com.vgl.draw.animation.core.ads.BottomAdBarScaffold
import com.vgl.draw.animation.core.ads.InListAds
import com.vgl.draw.animation.core.ads.NativeAdSlot
import com.vgl.draw.animation.core.ads.itemsWithInListAds
import com.vgl.draw.animation.core.ads.rememberInListAds

/**
 * The ads of the projects list (projects spec, section 5; plan T-010). The screen takes them as a parameter, so tests
 * pass fixed ads (they have no ads controller); the app uses [DefaultProjectsAdSlots].
 */
interface ProjectsAdSlots {
    /** The native medium card at the end of the empty state's column, under the buttons. */
    @Composable
    fun EmptyAd(modifier: Modifier)

    /** The bottom bar around the content of the list state; [content] gets the bar's space as bottom padding. */
    @Composable
    fun BottomBar(content: @Composable (PaddingValues) -> Unit)

    /** The grid's ads, remembered at the screen level, outside the grid. */
    @Composable
    fun rememberGridAds(): ProjectsGridAds
}

/** Adds the project cards to the grid of [columns] columns, with the grid's ads between them. */
interface ProjectsGridAds {
    fun cards(
        scope: LazyGridScope,
        cards: List<ProjectCardUi>,
        columns: Int,
        itemContent: @Composable LazyGridItemScope.(ProjectCardUi) -> Unit,
    )
}

/** The real ads, through :core:ads: each placement shows only while its `is_show_<code>` key is on. */
object DefaultProjectsAdSlots : ProjectsAdSlots {
    @Composable
    override fun EmptyAd(modifier: Modifier) {
        // Under every button of the screen: an ad coming or going moves no button, so no space is kept.
        NativeAdSlot(AdPlacement.PROJECTS_EMPTY_NATIVE, modifier)
    }

    @Composable
    override fun BottomBar(content: @Composable (PaddingValues) -> Unit) {
        BottomAdBarScaffold(AdPlacement.PROJECTS_BOTTOM, content = content)
    }

    @Composable
    override fun rememberGridAds(): ProjectsGridAds {
        val ads = rememberInListAds(AdPlacement.PROJECTS_GRID)
        return remember(ads) { InListProjectsGridAds(ads) }
    }
}

private class InListProjectsGridAds(private val ads: InListAds) : ProjectsGridAds {
    override fun cards(
        scope: LazyGridScope,
        cards: List<ProjectCardUi>,
        columns: Int,
        itemContent: @Composable LazyGridItemScope.(ProjectCardUi) -> Unit,
    ) {
        scope.itemsWithInListAds(
            items = cards,
            ads = ads,
            columns = columns,
            key = { it.id.value },
            adCellHeight = { PROJECT_GRID_AD_HEIGHT },
            itemContent = itemContent,
        )
    }
}

/**
 * Height of an ad cell in the grid: the smallest that keeps the card's media at 120 dp or more (ads-business-rules,
 * 5.7) at the default font scale, taller than a project card, whose row then grows (plan T-010, question 1).
 */
internal val PROJECT_GRID_AD_HEIGHT = 228.dp

/** The in-content placements of the projects list (MC-13): while one is on, the bottom bar has no floating layer. */
fun projectsInContentAds(): List<AdPlacement> = listOf(
    AdPlacement.PROJECTS_EMPTY_NATIVE,
    AdPlacement.PROJECTS_GRID,
    AdPlacement.RESTORE_MREC,
)
