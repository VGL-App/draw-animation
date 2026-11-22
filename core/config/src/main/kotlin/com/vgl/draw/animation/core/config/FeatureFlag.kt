package com.vgl.draw.animation.core.config

/**
 * Every feature flag of the app, declared in one place (ADR-0003).
 * Naming rule for remote keys: `feature_<name>_enabled`.
 * [defaultValue] is what the app uses until remote values are fetched. It is always `false`
 * (checked by a unit test): a feature stays hidden in production until it is approved.
 * [description] is one sentence in English, shown on Firebase and in docs/remote-config-keys.md (ADR-0046).
 * To see a feature while it is being built, enable it for the staging build only (see [LocallyEnabledFlags]).
 */
enum class FeatureFlag(val key: String, val description: String, val defaultValue: Boolean = false) {
    SAMPLE("feature_sample_enabled", "Shows the sample feature screen, the reference pattern for new features."),
    LOADING("feature_loading_enabled", "Shows the loading screen that prepares the app before the start screen."),
    ONBOARDING(
        "feature_onboarding_enabled",
        "Shows onboarding to users who have not finished it; off opens the project list directly.",
    ),
    PROJECTS("feature_projects_enabled", "Opens the project list and the Create project screen."),
    SETTINGS(
        "feature_settings_enabled",
        "Shows the settings button on the project list and opens the Settings screen, where users withdraw ad consent.",
    ),
    EDITOR("feature_editor_enabled", "Lets users open the drawing screen, virtual camera included."),
    EXPORT(
        "feature_export_enabled",
        "Shows the export button on the drawing and lesson complete screens and opens the Export screen.",
    ),
    LESSONS(
        "feature_lessons_enabled",
        "Shows the Lessons tab, the free lesson button on the empty project list and the Lessons screen.",
    ),
    ;

    /** The Remote Config key of this flag, listed with every other key of the app (ADR-0046). */
    val remoteKey: RemoteKey<Boolean> = RemoteKey.boolean(key, defaultValue, description)
}
