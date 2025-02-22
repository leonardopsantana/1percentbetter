

package com.onepercentbetter

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import com.onepercentbetter.FlavorDimension.contentType

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
@Suppress("EnumEntryName")
enum class OPBFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    demo(contentType, applicationIdSuffix = ".demo"),
    prod(contentType),
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: OPBFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            OPBFlavor.values().forEach { opbFlavor ->
                register(opbFlavor.name) {
                    dimension = opbFlavor.dimension.name
                    flavorConfigurationBlock(this, opbFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (opbFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = opbFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
