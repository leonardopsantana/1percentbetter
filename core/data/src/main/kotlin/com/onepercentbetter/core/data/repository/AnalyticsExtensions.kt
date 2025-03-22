package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.analytics.AnalyticsEvent
import com.onepercentbetter.core.analytics.AnalyticsEvent.Param
import com.onepercentbetter.core.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logDarkThemeConfigChanged(darkThemeConfigName: String) =
    logEvent(
        AnalyticsEvent(
            type = "dark_theme_config_changed",
            extras = listOf(
                Param(key = "dark_theme_config", value = darkThemeConfigName),
            ),
        ),
    )

internal fun AnalyticsHelper.logOnboardingStateChanged(shouldHideOnboarding: Boolean) {
    val eventType = if (shouldHideOnboarding) "onboarding_complete" else "onboarding_reset"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}
