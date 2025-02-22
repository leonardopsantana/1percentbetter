


plugins {
    alias(libs.plugins.onepercentbetter.jvm.library)
    alias(libs.plugins.onepercentbetter.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
