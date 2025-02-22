


plugins {
    alias(libs.plugins.onepercentbetter.android.library)
    alias(libs.plugins.onepercentbetter.android.library.jacoco)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.onepercentbetter.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)

    testImplementation(projects.core.testing)
}
