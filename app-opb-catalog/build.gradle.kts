


import com.onepercentbetter.FlavorDimension
import com.onepercentbetter.OPBFlavor


plugins {
    alias(libs.plugins.onepercentbetter.android.application)
    alias(libs.plugins.onepercentbetter.android.application.compose)
}

android {
    defaultConfig {
        applicationId = "com.onepercentbetter.opbcatalog"
        versionCode = 1
        versionName = "0.0.1" // X.Y.Z; X = Major, Y = minor, Z = Patch level

        // The UI catalog does not depend on content from the app, however, it depends on modules
        // which do, so we must specify a default value for the contentType dimension.
        missingDimensionStrategy(FlavorDimension.contentType.name, OPBFlavor.demo.name)
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    namespace = "com.onepercentbetter.opbcatalog"

    buildTypes {
        release {
            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.named("debug").get()
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)

    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
}

dependencyGuard {
    configuration("releaseRuntimeClasspath")
}
