



plugins {
    alias(libs.plugins.onepercentbetter.android.feature)
    alias(libs.plugins.onepercentbetter.android.library.compose)
    alias(libs.plugins.onepercentbetter.android.library.jacoco)
}

android {
    namespace = "com.onepercentbetter.feature.bookmarks"
}

dependencies {
    implementation(projects.core.data)

    testImplementation(projects.core.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
