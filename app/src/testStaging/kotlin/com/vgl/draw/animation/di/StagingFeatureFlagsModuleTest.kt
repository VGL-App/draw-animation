package com.vgl.draw.animation.di

import com.vgl.draw.animation.core.config.FeatureFlag
import dagger.Provides
import dagger.multibindings.IntoSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Flags turned on in staging builds (plan T-006, section 3.1). */
class StagingFeatureFlagsModuleTest {
    @Test
    fun stagingEnablesFiveFrameloopFlags_lessonsStaysOff() {
        // @LocallyEnabledFlags has BINARY retention, so reflection filters on @Provides and @IntoSet only.
        val methods = StagingFeatureFlagsModule::class.java.declaredMethods
        val providers = methods.filter {
            it.isAnnotationPresent(Provides::class.java) && it.isAnnotationPresent(IntoSet::class.java)
        }
        val keys = providers.map { (it.invoke(StagingFeatureFlagsModule) as FeatureFlag).key }

        // Every function of the module that returns a flag goes into the set.
        assertEquals(methods.filter { it.returnType == FeatureFlag::class.java }.toSet(), providers.toSet())
        // Control: reflection reads the right module, and no flag is enabled twice.
        assertTrue(keys.isNotEmpty())
        assertTrue(keys.toString(), "feature_sample_enabled" in keys)
        assertEquals(keys.toString(), keys.size, keys.toSet().size)
        // feature_lessons_enabled is left out until there is lesson content (T-054, lessons spec section 8).
        assertEquals(
            setOf(
                "feature_sample_enabled",
                "feature_loading_enabled",
                "feature_onboarding_enabled",
                "feature_projects_enabled",
                "feature_settings_enabled",
                "feature_editor_enabled",
                "feature_export_enabled",
            ),
            keys.toSet(),
        )
    }
}
