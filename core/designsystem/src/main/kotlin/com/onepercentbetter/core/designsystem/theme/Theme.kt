

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
    primary = Orange40,
    onPrimary = Color.White,
    primaryContainer = Orange90,
    onPrimaryContainer = Orange10,
    secondary = Orange40,
    onSecondary = Color.White,
    secondaryContainer = Orange90,
    onSecondaryContainer = Orange10,
    tertiary = Blue40,
    onTertiary = Color.White,
    tertiaryContainer = Blue90,
    onTertiaryContainer = Blue10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = DarkOrangeGray99,
    onBackground = DarkOrangeGray10,
    surface = DarkOrangeGray99,
    onSurface = DarkOrangeGray10,
    surfaceVariant = OrangeGray90,
    onSurfaceVariant = OrangeGray30,
    inverseSurface = DarkOrangeGray20,
    inverseOnSurface = DarkOrangeGray95,
    outline = OrangeGray50,
)

/**
 * Dark default theme color scheme
 */
@VisibleForTesting
val DarkDefaultColorScheme = darkColorScheme(
    primary = Orange80,
    onPrimary = Orange20,
    primaryContainer = Orange30,
    onPrimaryContainer = Orange90,
    secondary = Orange80,
    onSecondary = Orange20,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange90,
    tertiary = Blue80,
    onTertiary = Blue20,
    tertiaryContainer = Blue30,
    onTertiaryContainer = Blue90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = DarkOrangeGray10,
    onBackground = DarkOrangeGray90,
    surface = DarkOrangeGray10,
    onSurface = DarkOrangeGray90,
    surfaceVariant = OrangeGray30,
    onSurfaceVariant = OrangeGray80,
    inverseSurface = DarkOrangeGray90,
    inverseOnSurface = DarkOrangeGray10,
    outline = OrangeGray60,
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
        LocalTintTheme provides  TintTheme(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = OPBTypography,
            content = content,
        )
    }
}
