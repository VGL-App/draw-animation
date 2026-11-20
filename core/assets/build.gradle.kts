plugins {
    alias(libs.plugins.template.android.library)
    alias(libs.plugins.template.hilt)
}

android {
    namespace = "com.vgl.draw.animation.core.assets"
}

dependencies {
    implementation(project(":core:logging"))
    implementation(project(":core:network"))
}
