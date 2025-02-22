


plugins {
    alias(libs.plugins.onepercentbetter.android.library)
    alias(libs.plugins.onepercentbetter.hilt)
}

android {
    namespace = "com.onepercentbetter.core.sync.test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.data)
    implementation(projects.sync.work)
}
