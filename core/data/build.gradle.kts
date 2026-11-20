plugins {
    alias(libs.plugins.template.android.library)
    alias(libs.plugins.template.hilt)
}

android {
    namespace = "com.vgl.draw.animation.core.data"
}

dependencies {
    api(libs.androidx.datastore.preferences)
}
