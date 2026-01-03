package com.example.multiplayersudoku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
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
val AppTypography = Typography(
    // --- DISPLAY: Large, short, important text ---
    displayLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 45.sp,
        lineHeight = 52.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 36.sp,
        lineHeight = 44.sp,
    ),

    // --- HEADLINE: High-emphasis, branding moments ---
    headlineLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),

    // --- TITLE: Medium-emphasis, section headers ---
    titleLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),

    // --- BODY: Long passages and standard UI text ---
    bodyLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),

    // --- LABEL: Utilitarian text (Buttons, Tooltips, Captions) ---
    labelLarge = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = GoogleSansFlexFamily,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
)