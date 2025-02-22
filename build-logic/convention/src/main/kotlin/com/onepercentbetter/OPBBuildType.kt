

package com.onepercentbetter

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class OPBBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}
