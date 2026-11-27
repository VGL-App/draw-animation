package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.vgl.draw.animation.core.ads.AdFormat
import com.vgl.draw.animation.core.ads.AdPlacement
import com.vgl.draw.animation.core.ads.BottomAdBarDefaults
import com.vgl.draw.animation.core.ads.BottomAdBarLayout
import com.vgl.draw.animation.core.ads.NativeAdAssets
import com.vgl.draw.animation.core.ads.NativeAdCard
import com.vgl.draw.animation.core.ads.NativeAdSize
import com.vgl.draw.animation.core.ads.itemsWithFixedAds

/** Tag of the fake native ad in the bottom bar's slot. */
const val FAKE_BOTTOM_BAR_TAG = "fake_bottom_bar"

/** Tag of the [slot]-th fake card in the grid. */
fun fakeGridAdTag(slot: Int): String = "projects_grid_ad_$slot"

/** Texts of the design's ads (design/context/ProjectsScreen_empty_*.md, ProjectsScreen_list_*.md). */
val fakeEmptyAdAssets = NativeAdAssets(
    headline = "Sponsored app name",
    advertiser = "Advertiser",
    body = null,
    callToAction = "Install",
    icon = null,
    hasMedia = true,
)

val fakeBottomBarAssets = NativeAdAssets(
    headline = "Sponsored app name",
    advertiser = null,
    body = "Native ad, bottom bar",
    callToAction = "Install",
    icon = null,
    hasMedia = true,
)

/**
 * Ad slots with fixed ads (tests have no ads controller): a medium card under the empty state's buttons, the bottom
 * bar with a small card and no collapsible layer (MC-13: the screen has in-content ads), and a card after every 6
 * projects in the grid, [PROJECT_GRID_AD_HEIGHT] high. [receivedColumns] is the column count the grid passed.
 */
class FakeProjectsAdSlots : ProjectsAdSlots {
    var receivedColumns: Int? = null
        private set

    @Composable
    override fun EmptyAd(modifier: Modifier) {
        NativeAdCard(fakeEmptyAdAssets, NativeAdSize.MEDIUM, modifier)
    }

    @Composable
    override fun BottomBar(content: @Composable (PaddingValues) -> Unit) {
        BottomAdBarLayout(
            reservation = BottomAdBarDefaults.nativeReservation(),
            slot = { NativeAdCard(fakeBottomBarAssets, NativeAdSize.SMALL, it.testTag(FAKE_BOTTOM_BAR_TAG)) },
            layer = null,
            content = content,
        )
    }

    @Composable
    override fun rememberGridAds(): ProjectsGridAds = remember {
        object : ProjectsGridAds {
            override fun cards(
                scope: LazyGridScope,
                cards: List<ProjectCardUi>,
                columns: Int,
                itemContent: @Composable LazyGridItemScope.(ProjectCardUi) -> Unit,
            ) {
                receivedColumns = columns
                scope.itemsWithFixedAds(
                    items = cards,
                    format = AdFormat.NATIVE_IN_COLLECTION,
                    every = AdPlacement.PROJECTS_GRID.nativeEvery!!,
                    columns = columns,
                    key = { it.id.value },
                    adContent = { slot ->
                        NativeAdCard(
                            fakeEmptyAdAssets,
                            NativeAdSize.CARD,
                            Modifier.testTag(fakeGridAdTag(slot)),
                            cardHeight = PROJECT_GRID_AD_HEIGHT,
                        )
                    },
                    itemContent = itemContent,
                )
            }
        }
    }
}
