package com.example.multiplayersudoku.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.multiplayersudoku.R

@OptIn(ExperimentalTextApi::class)
val GoogleSansFlexFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
            FontVariation.width(100f)
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val FredokaFamily = FontFamily(
    Font(
        resId = R.font.fredoka,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),
            FontVariation.width(100f)
        )
    )
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val AppTypography = Typography(
    // --- DISPLAY ---
    displayLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayLargeEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        fontWeight = FontWeight.Bold
    ),
    displayMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 45.sp,
        lineHeight = 52.sp,
    ),
    displayMediumEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        fontWeight = FontWeight.Bold
    ),
    displaySmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 36.sp,
        lineHeight = 44.sp,
    ),
    displaySmallEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.Bold
    ),

    // --- HEADLINE ---
    headlineLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineLargeEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineMediumEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineSmallEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold
    ),

    // --- TITLE ---
    titleLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleLargeEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleMediumEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        fontWeight = FontWeight.Bold
    ),
    titleSmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmallEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Bold
    ),

    // --- BODY ---
    bodyLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyLargeEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Bold
    ),
    bodyMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodyMediumEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        fontWeight = FontWeight.Bold
    ),
    bodySmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    bodySmallEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold
    ),

    // --- LABEL ---
    labelLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelLargeEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Bold
    ),
    labelMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelMediumEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Bold
    ),
    labelSmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmallEmphasized = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Bold
    )
)