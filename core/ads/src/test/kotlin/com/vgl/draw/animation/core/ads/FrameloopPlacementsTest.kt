package com.vgl.draw.animation.core.ads

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The 22 ad placements of Frameloop (plan T-007): proposal table 9.1 and section 5 of the six specs. */
class FrameloopPlacementsTest {
    private val frameloop = listOf(
        AdPlacement.NATIVE_ONBOARDING,
        AdPlacement.PROJECTS_EMPTY_NATIVE,
        AdPlacement.PROJECTS_BOTTOM,
        AdPlacement.PROJECTS_GRID,
        AdPlacement.RESTORE_MREC,
        AdPlacement.CREATE_INTER,
        AdPlacement.NEW_PROJECT_NATIVE,
        AdPlacement.SETTINGS_BOTTOM,
        AdPlacement.EDITOR_EXIT_INTER,
        AdPlacement.EDITOR_RW_LAYER,
        AdPlacement.EDITOR_RW_BRUSHES,
        AdPlacement.EDITOR_RW_SOUNDS,
        AdPlacement.EXPORT_OPTIONS_MREC,
        AdPlacement.EXPORT_WAIT_MREC,
        AdPlacement.EXPORT_DONE_NATIVE,
        AdPlacement.EXPORT_INTERSTITIAL,
        AdPlacement.EXPORT_RW_EXTRA,
        AdPlacement.LESSONS_GRID,
        AdPlacement.LESSONS_BOTTOM,
        AdPlacement.LESSON_START_INTER,
        AdPlacement.LESSON_DONE_NATIVE,
        AdPlacement.LESSON_NEXT_INTER,
    )

    @Test
    fun placements_matchSpecTables() {
        val expected = mapOf(
            "native_onboarding" to "native_step",
            "projects_empty_native" to "native_medium",
            "projects_bottom" to "native_small",
            "projects_grid" to "native_in_collection",
            "restore_mrec" to "banner_mrec",
            "create_inter" to "interstitial",
            "new_project_native" to "native_medium",
            "settings_bottom" to "native_small",
            "editor_exit_inter" to "interstitial",
            "editor_rw_layer" to "rewarded",
            "editor_rw_brushes" to "rewarded",
            "editor_rw_sounds" to "rewarded",
            "export_options_mrec" to "banner_mrec",
            "export_wait_mrec" to "banner_mrec",
            "export_done_native" to "native_medium",
            "export_interstitial" to "interstitial",
            "export_rw_extra" to "rewarded",
            "lessons_grid" to "native_in_collection",
            "lessons_bottom" to "native_small",
            "lesson_start_inter" to "interstitial",
            "lesson_done_native" to "native_medium",
            "lesson_next_inter" to "interstitial",
        )
        val declared = AdPlacement.entries
            .filterNot { it.stagingOnly || it.appWide || it == AdPlacement.SAMPLE_NATIVE_SMALL }
            .associate { it.key to it.format.code }
        assertEquals(expected, declared)
        assertEquals(expected.keys.toList(), frameloop.map { it.key })
    }

    @Test
    fun switches_offByDefault_inKeyList() {
        val all = AdsSettings.all()
        frameloop.forEach { p ->
            val key = AdsSettings.placementEnabled(p)
            assertEquals("is_show_${p.key}", key.name)
            assertFalse(p.key, key.default)
            assertTrue(p.key, all.contains(key))
        }
        val names = frameloop.map { AdsSettings.placementEnabled(it).name }
        assertEquals(22, names.toSet().size)
    }

    @Test
    fun grids_nativeEvery() {
        assertEquals(6, AdPlacement.PROJECTS_GRID.nativeEvery)
        assertEquals(4, AdPlacement.LESSONS_GRID.nativeEvery)
        assertEquals(AdFormat.NATIVE_IN_COLLECTION, AdPlacement.PROJECTS_GRID.format)
        assertEquals(AdFormat.NATIVE_IN_COLLECTION, AdPlacement.LESSONS_GRID.format)
        val projects = AdsSettings.nativeEvery(AdPlacement.PROJECTS_GRID)!!
        assertEquals("native_every_projects_grid", projects.name)
        assertEquals(6L, projects.default)
        val lessons = AdsSettings.nativeEvery(AdPlacement.LESSONS_GRID)!!
        assertEquals("native_every_lessons_grid", lessons.name)
        assertEquals(4L, lessons.default)
        val others = frameloop - setOf(AdPlacement.PROJECTS_GRID, AdPlacement.LESSONS_GRID)
        assertEquals(20, others.size)
        others.forEach { p ->
            assertNull(p.key, p.nativeEvery)
            assertNull(p.key, AdsSettings.nativeEvery(p))
        }
    }

    @Test
    fun rewarded_allowAfter2() {
        val rewarded = listOf(
            AdPlacement.EDITOR_RW_LAYER,
            AdPlacement.EDITOR_RW_BRUSHES,
            AdPlacement.EDITOR_RW_SOUNDS,
            AdPlacement.EXPORT_RW_EXTRA,
        )
        rewarded.forEach { p ->
            assertEquals(p.key, AdFormat.REWARDED, p.format)
            val policy = p.declaration.effectiveNoAdPolicy
            assertEquals(p.key, RewardedNoAdPolicy.AllowAfter(2), policy)
            assertFalse(p.key, grantsWithoutAd(policy, 1))
            assertTrue(p.key, grantsWithoutAd(policy, 2))
        }
        val gallery = AdPlacement.GALLERY_RW_ACTION.declaration.effectiveNoAdPolicy
        assertFalse(grantsWithoutAd(gallery, 2))
        assertEquals(rewarded, frameloop.filter { it.format == AdFormat.REWARDED })
    }

    @Test
    fun onlyNativeOnboarding_firstOpenOnly() {
        assertTrue(AdPlacement.NATIVE_ONBOARDING.declaration.firstOpenOnly)
        assertEquals(AdFormat.NATIVE_STEP, AdPlacement.NATIVE_ONBOARDING.format)
        val others = frameloop - AdPlacement.NATIVE_ONBOARDING
        assertEquals(21, others.size)
        others.forEach { assertFalse(it.key, it.declaration.firstOpenOnly) }
    }

    @Test
    fun interstitials_defaultIntervalGroup() {
        val interstitials = listOf(
            AdPlacement.CREATE_INTER,
            AdPlacement.EDITOR_EXIT_INTER,
            AdPlacement.EXPORT_INTERSTITIAL,
            AdPlacement.LESSON_START_INTER,
            AdPlacement.LESSON_NEXT_INTER,
        )
        interstitials.forEach { p ->
            assertEquals(p.key, AdIntervalGroup.DEFAULT, p.intervalGroup)
            assertTrue(p.key, p.declaration.readsInterval())
            assertTrue(p.key, p.declaration.pushesIntervalMark())
        }
        assertEquals(interstitials, frameloop.filter { it.format == AdFormat.INTERSTITIAL })
    }

    @Test
    fun noneAppWideOrStagingOnly() {
        frameloop.forEach { p ->
            assertFalse(p.key, p.appWide)
            assertFalse(p.key, p.stagingOnly)
        }
    }
}
