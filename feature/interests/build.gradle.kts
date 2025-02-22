



plugins {
    alias(libs.plugins.onepercentbetter.android.feature)
    alias(libs.plugins.onepercentbetter.android.library.compose)
    alias(libs.plugins.onepercentbetter.android.library.jacoco)
}
android {
    namespace = "com.onepercentbetter.feature.interests"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)

    testImplementation(projects.core.testing)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
