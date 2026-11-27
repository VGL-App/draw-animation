package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vgl.draw.animation.core.ads.AdPlacement

/** The ads of the projects list (plan T-010). Skeleton: draws nothing yet. */
interface ProjectsAdSlots {
    @Composable
    fun EmptyAd(modifier: Modifier)

    @Composable
    fun BottomBar(content: @Composable (PaddingValues) -> Unit)

    @Composable
    fun rememberGridAds(): ProjectsGridAds
}

/** Adds the project cards, with their ads, to the grid. */
interface ProjectsGridAds {
    fun cards(
        scope: LazyGridScope,
        cards: List<ProjectCardUi>,
        columns: Int,
        itemContent: @Composable LazyGridItemScope.(ProjectCardUi) -> Unit,
    )
}

object DefaultProjectsAdSlots : ProjectsAdSlots {
    @Composable
    override fun EmptyAd(modifier: Modifier) = Unit

    @Composable
    override fun BottomBar(content: @Composable (PaddingValues) -> Unit) = content(PaddingValues())

    @Composable
    override fun rememberGridAds(): ProjectsGridAds = object : ProjectsGridAds {
        override fun cards(
            scope: LazyGridScope,
            cards: List<ProjectCardUi>,
            columns: Int,
            itemContent: @Composable LazyGridItemScope.(ProjectCardUi) -> Unit,
        ) {
            scope.items(cards, key = { it.id.value }, itemContent = itemContent)
        }
    }
}

internal val PROJECT_GRID_AD_HEIGHT = 228.dp

fun projectsInContentAds(): List<AdPlacement> = emptyList()
