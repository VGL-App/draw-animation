plugins {
    alias(libs.plugins.template.android.feature)
}

android {
    namespace = "com.vgl.draw.animation.feature.export"
}

dependencies {
    implementation(project(":core:project"))
}
