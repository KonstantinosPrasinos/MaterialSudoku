package com.example.multiplayersudoku.utils

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color): Modifier = this.drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    val width = size.width
    val height = size.height - strokeWidthPx / 2

    drawLine(
        color = color,
        start = Offset(x = 0f, y = height),
        end = Offset(x = width, y = height),
        strokeWidth = strokeWidthPx
    )
}

fun Modifier.topBorder(strokeWidth: Dp, color: Color): Modifier = this.drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    val width = size.width
    val y = strokeWidthPx / 2

    drawLine(
        color = color,
        start = Offset(x = 0f, y = y),
        end = Offset(x = width, y = y),
        strokeWidth = strokeWidthPx
    )
}

fun Modifier.leftBorder(strokeWidth: Dp, color: Color): Modifier = this.drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    val height = size.height
    val x = strokeWidthPx / 2

    drawLine(
        color = color,
        start = Offset(x = x, y = 0f),
        end = Offset(x = x, y = height),
        strokeWidth = strokeWidthPx
    )
}

fun Modifier.rightBorder(strokeWidth: Dp, color: Color): Modifier = this.drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    val height = size.height
    val x = size.width - strokeWidthPx / 2

    drawLine(
        color = color,
        start = Offset(x = x, y = 0f),
        end = Offset(x = x, y = height),
        strokeWidth = strokeWidthPx
    )
}

fun Modifier.frostedGlass(
    blurRadius: Dp = 20.dp,
    backgroundColor: Color = Color.Unspecified
): Modifier = this.graphicsLayer {
    val radiusPx = blurRadius.toPx()
    renderEffect = android.graphics.RenderEffect
        .createBlurEffect(radiusPx, radiusPx, android.graphics.Shader.TileMode.CLAMP)
        .asComposeRenderEffect()
}.then(
    if (backgroundColor != Color.Unspecified) Modifier.background(backgroundColor) else Modifier
)