plugins {
    alias(libs.plugins.template.android.feature)
}

android {
    namespace = "com.vgl.draw.animation.feature.projects"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:project"))
}
