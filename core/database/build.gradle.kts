



plugins {
    alias(libs.plugins.onepercentbetter.android.library)
    alias(libs.plugins.onepercentbetter.android.library.jacoco)
    alias(libs.plugins.onepercentbetter.android.room)
    alias(libs.plugins.onepercentbetter.hilt)
}

android {
    namespace = "com.onepercentbetter.core.database"
}

dependencies {
    api(projects.core.model)

    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
