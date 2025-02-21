
plugins {
    alias(libs.plugins.onepercentbetter.android.library)
    alias(libs.plugins.onepercentbetter.android.library.compose)
    alias(libs.plugins.onepercentbetter.hilt)
}

android {
    namespace = "com.onepercentbetter.core.screenshottesting"
}

dependencies {
    api(libs.bundles.androidx.compose.ui.test)
    api(libs.roborazzi)
    api(libs.roborazzi.accessibility.check)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.activity.compose)
    implementation(libs.robolectric)
    implementation(projects.core.designsystem)
}
