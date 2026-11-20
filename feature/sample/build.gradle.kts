plugins {
    alias(libs.plugins.template.android.feature)
}

android {
    namespace = "com.vgl.draw.animation.feature.sample"
}

dependencies {
    implementation(project(":core:data"))
}
