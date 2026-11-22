plugins {
    alias(libs.plugins.template.android.library)
    alias(libs.plugins.template.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vgl.draw.animation.core.lessons"
}

dependencies {
    implementation(project(":core:assets"))
    implementation(project(":core:config"))
    implementation(project(":core:data"))
    implementation(project(":core:logging"))
    implementation(libs.kotlinx.serialization.json)
}
