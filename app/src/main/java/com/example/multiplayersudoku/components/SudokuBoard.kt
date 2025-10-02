package com.example.multiplayersudoku.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.SudokuBoardData

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SudokuBoard(
    boardData: SudokuBoardData,
    selectedTileIndices: List<Int?>,
    selectTile: (Int, Int) -> Unit,
    isPaused: Boolean,
    togglePaused: () -> Unit
) {
    Card {
        Box {
            // 1. The Sudoku Grid is the first child, drawn at the bottom.
            Column {
                for (row in 0 until 9) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 9) {
                            val tileData = boardData.board[row][col]
                            val interactionSource = remember { MutableInteractionSource() }

                            SudokuTile(
                                tileData = tileData,
                                selectedTileIndices = selectedTileIndices,
                                interactionSource = interactionSource,
                                onClick = { selectTile(row, col) },
                                modifier = Modifier.weight(1f),
                                selectedNumber = if (selectedTileIndices[0] != null) boardData.board[selectedTileIndices[0]!!][selectedTileIndices[1]!!].value else null,
                                isPaused = isPaused
                            )
                        }
                    }
                }
            }

            this@Card.AnimatedVisibility(
                visible = isPaused,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size((9 * 40).dp)
                        .background(
                            MaterialTheme.colorScheme.scrim.copy(
                                alpha = 0.3f
                            )
                        ), // Semi-transparent background
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { togglePaused() },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text(
                            "Paused",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}