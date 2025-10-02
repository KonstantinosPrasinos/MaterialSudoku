package com.example.multiplayersudoku.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun InfoBar(
    mistakes: Int = 1,
    hints: Int = 1,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Mistakes:",
                style = MaterialTheme.typography.labelLargeEmphasized,
            )
            (1..3).forEach { number ->
                val animatedColor: Color by animateColorAsState(
                    if (number <= mistakes) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                    label = "color"
                )

                val shape = when (number) {
                    1 -> {
                        MaterialShapes.Gem.toShape()
                    }

                    2 -> {
                        MaterialShapes.Sunny.toShape()
                    }

                    else -> {
                        MaterialShapes.Triangle.toShape()
                    }
                }

                Box(
                    modifier = Modifier
                        .size(14.dp) // Sets the size to 10dp x 10dp
                        .background(
                            color = animatedColor,
                            shape = shape
                        )
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Hints:",
                style = MaterialTheme.typography.labelLargeEmphasized,
            )
            (1..3).forEach { number ->
                val animatedColor: Color by animateColorAsState(
                    if (number <= hints) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    label = "color"
                )

                val shape = when (number) {
                    1 -> {
                        MaterialShapes.Gem.toShape()
                    }

                    2 -> {
                        MaterialShapes.Sunny.toShape()
                    }

                    else -> {
                        MaterialShapes.Triangle.toShape()
                    }
                }

                Box(
                    modifier = Modifier
                        .size(14.dp) // Sets the size to 10dp x 10dp
                        .background(
                            color = animatedColor,
                            shape = shape
                        )
                )
            }
        }
    }
}


