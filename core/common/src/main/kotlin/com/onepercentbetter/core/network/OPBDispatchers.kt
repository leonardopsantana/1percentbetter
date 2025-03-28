

package com.onepercentbetter.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val opbDispatcher: OPBDispatchers)

enum class OPBDispatchers {
    Default,
    IO,
}
