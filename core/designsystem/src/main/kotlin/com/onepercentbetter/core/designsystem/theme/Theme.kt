

package com.onepercentbetter.core.designsystem.theme

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Light default theme color scheme
 */
@VisibleForTesting
val LightDefaultColorScheme = lightColorScheme(
    primary = Yellow40,
    onPrimary = Color.White,
    primaryContainer = Yellow80,
    onPrimaryContainer = DarkYellowGray10,
    secondary = Yellow40,
    onSecondary = Color.White,
    secondaryContainer = Yellow90,
    onSecondaryContainer = Yellow10,
    tertiary = Blue40,
    onTertiary = Color.White,
    tertiaryContainer = Blue90,
    onTertiaryContainer = Blue10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = DarkYellowGray99,
    onBackground = DarkYellowGray10,
    surface = DarkYellowGray99,
    onSurface = DarkYellowGray10,
    surfaceVariant = YellowGray90,
    onSurfaceVariant = YellowGray30,
    inverseSurface = DarkYellowGray20,
    inverseOnSurface = DarkYellowGray95,
    outline = YellowGray50,
)

/**
 * Dark default theme color scheme
 */
@VisibleForTesting
val DarkDefaultColorScheme = darkColorScheme(
    primary = Yellow80,
    onPrimary = Yellow20,
    primaryContainer = Yellow30,
    onPrimaryContainer = Yellow90,
    secondary = Yellow80,
    onSecondary = Yellow20,
    secondaryContainer = Yellow30,
    onSecondaryContainer = Yellow90,
    tertiary = Blue80,
    onTertiary = Blue20,
    tertiaryContainer = Blue30,
    onTertiaryContainer = Blue90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = DarkYellowGray10,
    onBackground = DarkYellowGray90,
    surface = DarkYellowGray10,
    onSurface = DarkYellowGray90,
    surfaceVariant = YellowGray30,
    onSurfaceVariant = YellowGray80,
    inverseSurface = DarkYellowGray90,
    inverseOnSurface = DarkYellowGray10,
    outline = YellowGray60,
)

/**
 * One percent better theme.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 */
@Composable
fun OPBTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Color scheme
    val colorScheme = if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme

    // Gradient colors
    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )

    // Background theme
    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )
    val backgroundTheme = when {
        else -> defaultBackgroundTheme
    }

    // Composition locals
    CompositionLocalProvider(
        LocalGradientColors provides defaultGradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides TintTheme(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = OPBTypography,
            content = content,
        )
    }
}
