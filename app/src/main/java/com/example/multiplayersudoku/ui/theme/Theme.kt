package com.example.multiplayersudoku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme()

@Immutable
data class ExtendedColorScheme(
    val success: Color = Color.Unspecified,
    val win: Color = Color(0xFFF9CA24),

    // Teal
    val tealBg: Color = Color.Unspecified,
    val tealIconBg: Color = Color.Unspecified,
    val tealText: Color = Color.Unspecified,

    // Purple
    val purpleBg: Color = Color.Unspecified,
    val purpleIconBg: Color = Color.Unspecified,
    val purpleText: Color = Color.Unspecified,

    // Blue
    val blueBg: Color = Color.Unspecified,
    val blueIconBg: Color = Color.Unspecified,
    val blueText: Color = Color.Unspecified,

    // Red
    val redBg: Color = Color.Unspecified,
    val redIconBg: Color = Color.Unspecified,
    val redText: Color = Color.Unspecified,
)

private val LightExtendedColorScheme = ExtendedColorScheme(
    success = Color.Unspecified,
    win = Color(0xFFF9CA24),

    tealBg = LightTealBg,
    tealIconBg = LightTealIconBg,
    tealText = LightTealText,

    purpleBg = LightPurpleBg,
    purpleIconBg = LightPurpleIconBg,
    purpleText = LightPurpleText,

    blueBg = LightBlueBg,
    blueIconBg = LightBlueIconBg,
    blueText = LightBlueText,

    redBg = LightRedBg,
    redIconBg = LightRedIconBg,
    redText = LightRedText,
)

private val DarkExtendedColorScheme = ExtendedColorScheme(
    success = Color.Unspecified,
    win = Color(0xFFF9CA24),

    tealBg = DarkTealBg,
    tealIconBg = DarkTealIconBg,
    tealText = DarkTealText,

    purpleBg = DarkPurpleBg,
    purpleIconBg = DarkPurpleIconBg,
    purpleText = DarkPurpleText,

    blueBg = DarkBlueBg,
    blueIconBg = DarkBlueIconBg,
    blueText = DarkBlueText,

    redBg = DarkRedBg,
    redIconBg = DarkRedIconBg,
    redText = DarkRedText,
)

val LocalExtendedColorScheme = staticCompositionLocalOf { ExtendedColorScheme() }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MultiplayerSudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtendedColorScheme else LightExtendedColorScheme

    CompositionLocalProvider(LocalExtendedColorScheme provides extendedColors) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            motionScheme = MotionScheme.expressive(),
            content = content,
        )
    }
}

val MaterialTheme.extendedColors: ExtendedColorScheme
    @Composable
    get() = LocalExtendedColorScheme.current