package com.vgl.draw.animation.core.config

import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultFeatureFlagsTest {
    private val remote = FakeRemoteFlagSource()

    /** Remote keys of the Frameloop flags, from section 8 of each feature spec (plan T-006). */
    private val frameloopKeys = listOf(
        "feature_onboarding_enabled",
        "feature_projects_enabled",
        "feature_settings_enabled",
        "feature_editor_enabled",
        "feature_export_enabled",
        "feature_lessons_enabled",
    )

    private fun flags(locallyEnabled: Set<FeatureFlag> = emptySet()) = DefaultFeatureFlags(locallyEnabled, remote)

    @Test
    fun everyFlag_isOffByDefault() {
        // Rule (ADR-0003): a feature stays hidden in production until it is approved for release.
        FeatureFlag.entries.forEach { flag ->
            assertFalse("Flag ${flag.name} must default to off", flag.defaultValue)
        }
    }

    @Test
    fun withoutLocalOrRemoteValues_everyFlagIsOff() = runTest {
        val flags = flags()
        FeatureFlag.entries.forEach { flag -> assertFalse(flags.enabled(flag).first()) }
    }

    @Test
    fun locallyEnabledFlag_isOn() = runTest {
        assertTrue(flags(locallyEnabled = setOf(FeatureFlag.SAMPLE)).enabled(FeatureFlag.SAMPLE).first())
    }

    @Test
    fun locallyEnabledFlag_staysOnWhenRemoteSaysOff() = runTest {
        remote.booleans.value = mapOf(FeatureFlag.SAMPLE.key to false)
        assertTrue(flags(locallyEnabled = setOf(FeatureFlag.SAMPLE)).enabled(FeatureFlag.SAMPLE).first())
    }

    @Test
    fun remoteValueTrue_turnsFlagOn() = runTest {
        remote.booleans.value = mapOf(FeatureFlag.SAMPLE.key to true)
        assertTrue(flags().enabled(FeatureFlag.SAMPLE).first())
    }

    @Test
    fun remoteValueActivatedLater_reachesCollectors() = runTest {
        flags().enabled(FeatureFlag.SAMPLE).test {
            assertFalse(awaitItem())
            remote.booleans.value = mapOf(FeatureFlag.SAMPLE.key to true)
            assertTrue(awaitItem())
            remote.booleans.value = emptyMap()
            assertFalse(awaitItem())
        }
    }

    @Test
    fun everyFlag_remoteKeyMatchesKeyAndDefault() {
        FeatureFlag.entries.forEach { flag ->
            assertEquals(flag.key, flag.remoteKey.name)
            assertEquals(false, flag.remoteKey.default)
            assertEquals(RemoteKey.Type.BOOLEAN, flag.remoteKey.type)
        }
    }

    @Test
    fun everyFlagKey_followsNamingRule() {
        FeatureFlag.entries.forEach { flag ->
            assertTrue(
                "Flag key '${flag.key}' must match feature_<name>_enabled",
                Regex("^feature_[a-z0-9_]+_enabled$").matches(flag.key),
            )
        }
    }

    @Test
    fun frameloopFlags_areDeclaredOnceWithSpecKeys() {
        frameloopKeys.forEach { key ->
            assertEquals(key, 1, FeatureFlag.entries.count { it.key == key })
        }
        assertEquals(FeatureFlag.entries.size, FeatureFlag.entries.map { it.key }.toSet().size)
    }

    @Test
    fun sampleAndLoadingFlags_stayDeclared() {
        // Behavior kept by plan T-006: adding Frameloop flags does not replace the sample and loading flags.
        assertTrue(FeatureFlag.entries.any { it.key == "feature_sample_enabled" })
        assertTrue(FeatureFlag.entries.any { it.key == "feature_loading_enabled" })
    }

    @Test
    fun eachFrameloopFlag_followsOnlyItsOwnRemoteValue() = runTest {
        frameloopKeys.forEach { key ->
            val flag = FeatureFlag.entries.single { it.key == key }
            val flags = flags()
            flags.enabled(flag).test {
                assertFalse(key, awaitItem())
                remote.booleans.value = mapOf(key to true)
                assertTrue(key, awaitItem())
                // Control: the remote value of this flag does not turn any other flag on.
                FeatureFlag.entries.filter { it != flag }.forEach { other ->
                    assertFalse("${other.key} follows $key", flags.enabled(other).first())
                }
                remote.booleans.value = emptyMap()
                assertFalse(key, awaitItem())
            }
        }
    }
}
