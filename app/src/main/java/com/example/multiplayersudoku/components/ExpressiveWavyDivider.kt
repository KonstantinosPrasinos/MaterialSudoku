package com.example.multiplayersudoku.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth;
import androidx.compose.foundation.layout.height;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.drawWithCache;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Path;
import androidx.compose.ui.graphics.drawscope.Stroke;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.dp;
import kotlin.math.sin;

@Composable
fun ExpressiveWavyDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    thickness: Dp = 2.dp,
    waveLength: Dp = 16.dp,
    amplitude: Dp = 4.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(amplitude * 2) // Reserve just enough height for the crests/troughs
            .drawWithCache {
                val path = Path()
                val waveLengthPx = waveLength.toPx()
                val amplitudePx = amplitude.toPx()
                val thicknessPx = thickness.toPx()

                onDrawWithContent {
                    path.reset()
                    val midY = size.height / 2f

                    // Start path on the left edge
                    path.moveTo(0f, midY)

                    // Plot points along the width of the separator using a sine wave
                    var x = 0f
                    while (x <= size.width) {
                        // Standard sine formula mapping pixels to radians
                        val y = midY + amplitudePx * sin((x / waveLengthPx) * (2f * Math.PI.toFloat()))
                        path.lineTo(x, y)
                        x += 1f // Increment pixel by pixel for smoothness
                    }

                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = thicknessPx)
                    )
                }
            }
    ) {
        // Layout structure handled explicitly via drawWithCache
    }
}