plugins {
    alias(libs.plugins.template.android.library)
    alias(libs.plugins.template.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vgl.draw.animation.core.project"
}

dependencies {
    implementation(project(":core:logging"))
    implementation(libs.kotlinx.serialization.json)
}
