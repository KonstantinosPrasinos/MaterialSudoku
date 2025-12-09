package com.example.multiplayersudoku.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.Difficulty

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun DifficultyPicker(
    onDifficultySelected: (Difficulty) -> Unit = {},
    selectedDifficulty: Difficulty? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedCard(
            onClick = {
                onDifficultySelected(Difficulty.EASY)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            BoxWithConstraints {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val squareDimensions = this@BoxWithConstraints.maxWidth

                    val backgroundColor: Color by animateColorAsState(
                        if (selectedDifficulty === Difficulty.EASY) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        label = "easyBackgroundColor"
                    )

                    Surface(
                        color = backgroundColor,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(squareDimensions)
                            .padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(squareDimensions)
                        ) {
                            val remainingWidth = squareDimensions - 20.dp // 20 for padding

                            Box(
                                modifier = Modifier.size(squareDimensions),
                                contentAlignment = Alignment.Center
                            ) {
                                val shape = MaterialShapes.SoftBurst.toShape()

                                Box(
                                    modifier = Modifier
                                        .size(remainingWidth / 3)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = shape
                                        )
                                )
                            }
                            this@Column.AnimatedVisibility(
                                visible = selectedDifficulty == Difficulty.EASY,
                                modifier = Modifier.align(Alignment.TopEnd),
                                enter = expandIn(
                                    // Expands from the bottom-left corner of the surface
                                    expandFrom = Alignment.BottomStart,
                                    // Start with a very small size
                                    initialSize = { androidx.compose.ui.unit.IntSize(1, 1) }
                                ) + fadeIn(),
                                exit = shrinkOut() + fadeOut()
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(remainingWidth),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Medium difficulty selected",
                                        Modifier
                                            .size(remainingWidth / 4)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "Easy",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
        OutlinedCard(
            onClick = {
                onDifficultySelected(Difficulty.MEDIUM)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            BoxWithConstraints {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val squareDimensions = this@BoxWithConstraints.maxWidth

                    val backgroundColor: Color by animateColorAsState(
                        if (selectedDifficulty === Difficulty.MEDIUM) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        label = "easyBackgroundColor"
                    )

                    Surface(
                        color = backgroundColor,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(squareDimensions)
                            .padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(squareDimensions)
                        ) {
                            val remainingWidth = squareDimensions - 20.dp // 20 for padding

                            Box(
                                modifier = Modifier.size(squareDimensions),
                                contentAlignment = Alignment.Center
                            ) {
                                val shape = MaterialShapes.SoftBurst.toShape()

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(remainingWidth / 9),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(remainingWidth / 3)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = shape
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(remainingWidth / 3)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = shape
                                            )
                                    )
                                }
                            }
                            this@Column.AnimatedVisibility(
                                visible = selectedDifficulty == Difficulty.MEDIUM,
                                modifier = Modifier.align(Alignment.TopEnd),
                                enter = expandIn(
                                    // Expands from the bottom-left corner of the surface
                                    expandFrom = Alignment.BottomStart,
                                    // Start with a very small size
                                    initialSize = { androidx.compose.ui.unit.IntSize(1, 1) }
                                ) + fadeIn(),
                                exit = shrinkOut() + fadeOut()
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(remainingWidth),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Medium difficulty selected",
                                        Modifier
                                            .size(remainingWidth / 4)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "Medium",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
        OutlinedCard(
            onClick = {
                onDifficultySelected(Difficulty.HARD)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            BoxWithConstraints {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val squareDimensions = this@BoxWithConstraints.maxWidth

                    val backgroundColor: Color by animateColorAsState(
                        if (selectedDifficulty === Difficulty.HARD) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        label = "easyBackgroundColor"
                    )

                    Surface(
                        color = backgroundColor,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(squareDimensions)
                            .padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(squareDimensions)
                        ) {
                            val remainingWidth = squareDimensions - 20.dp // 20 for padding

                            Box(
                                modifier = Modifier.size(squareDimensions),
                                contentAlignment = Alignment.Center
                            ) {
                                val shape = MaterialShapes.SoftBurst.toShape()

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(remainingWidth / 3)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = shape
                                            )
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy((remainingWidth.value / 9).dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(remainingWidth / 3)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = shape
                                                )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(remainingWidth / 3)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = shape
                                                )
                                        )
                                    }
                                }
                            }
                            this@Column.AnimatedVisibility(
                                visible = selectedDifficulty == Difficulty.HARD,
                                modifier = Modifier.align(Alignment.TopEnd),
                                enter = expandIn(
                                    // Expands from the bottom-left corner of the surface
                                    expandFrom = Alignment.BottomStart,
                                    // Start with a very small size
                                    initialSize = { androidx.compose.ui.unit.IntSize(1, 1) }
                                ) + fadeIn(),
                                exit = shrinkOut() + fadeOut()
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(remainingWidth),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Medium difficulty selected",
                                        Modifier
                                            .size(remainingWidth / 4)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "Hard",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}