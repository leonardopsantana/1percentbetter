

plugins {
    alias(libs.plugins.onepercentbetter.android.feature)
    alias(libs.plugins.onepercentbetter.android.library.compose)
    alias(libs.plugins.onepercentbetter.android.library.jacoco)
}

android {
    namespace = "com.onepercentbetter.feature.topic"
}

dependencies {
    implementation(projects.core.data)

    testImplementation(projects.core.testing)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
