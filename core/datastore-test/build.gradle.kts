


plugins {
    alias(libs.plugins.onepercentbetter.android.library)
    alias(libs.plugins.onepercentbetter.hilt)
}

android {
    namespace = "com.onepercentbetter.core.datastore.test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
}
