plugins {
    alias(libs.plugins.template.android.library)
    alias(libs.plugins.template.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vgl.draw.animation.core.project"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:logging"))
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext.junit)
}
