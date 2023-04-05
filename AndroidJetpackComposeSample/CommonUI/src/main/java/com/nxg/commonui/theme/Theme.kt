package com.nxg.commonui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * fun darkColors(
 *   primary: Color = Color(0xFFBB86FC),
 *   primaryVariant: Color = Color(0xFF3700B3),
 *   secondary: Color = Color(0xFF03DAC6),
 *   secondaryVariant: Color = secondary,
 *   background: Color = Color(0xFF121212),
 *   surface: Color = Color(0xFF121212),
 *   error: Color = Color(0xFFCF6679),
 *   onPrimary: Color = Color.Black,
 *   onSecondary: Color = Color.Black,
 *   onBackground: Color = Color.White,
 *   onSurface: Color = Color.White,
 *   onError: Color = Color.Black
 *   )
 */
private val DarkColorPalette = darkColors(
    primary = DarkColor.primary,
    primaryVariant = DarkColor.primaryVariant,
    secondary = DarkColor.secondary,
    secondaryVariant = DarkColor.secondaryVariant,
    background = DarkColor.background,
    surface = DarkColor.surface,
    error = DarkColor.error,
    onPrimary = DarkColor.primary,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = ColorError.Primary,
)

/**
 * fun lightColors(
 *   primary: Color = Color(0xFF6200EE),
 *   primaryVariant: Color = Color(0xFF3700B3),
 *   secondary: Color = Color(0xFF03DAC6),
 *   secondaryVariant: Color = Color(0xFF018786),
 *   background: Color = Color.White,
 *   surface: Color = Color.White,
 *   error: Color = Color(0xFFB00020),
 *   onPrimary: Color = Color.White,
 *   onSecondary: Color = Color.Black,
 *   onBackground: Color = Color.Black,
 *   onSurface: Color = Color.Black,
 *   onError: Color = Color.White
 *   )
 */
private val LightColorPalette = lightColors(
    primary = ColorPrimary.Primary,
    primaryVariant = ColorPrimary.Primary,
    secondary = ColorPrimary.Dark,
    secondaryVariant = ColorPrimary.Dark,
    background = ColorBackground.Primary,
    surface = ColorBackground.Primary,
    error = ColorError.Primary,
    onPrimary = ColorBackground.Primary,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = ColorError.Light,
)

@Composable
fun AndroidJetpackComposeSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
