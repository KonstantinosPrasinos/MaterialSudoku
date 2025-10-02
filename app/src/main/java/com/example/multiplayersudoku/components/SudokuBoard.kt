package com.example.multiplayersudoku.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.multiplayersudoku.classes.SudokuBoardData

@Composable
fun SudokuBoard(boardData: SudokuBoardData, modifier: Modifier = Modifier) {
    var selectedTileIndices by remember { mutableStateOf<List<Int?>>(listOf(null, null)) }

    fun selectTile(row: Int, col: Int) {
        if (row == selectedTileIndices[0] && col == selectedTileIndices[1]) {
            selectedTileIndices = listOf(null, null)
        } else {
            selectedTileIndices = listOf(row, col)
        }
    }

    Card {
        Column {
            for (row in 0 until 9) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 9) {
                        val tileData = boardData.board[row][col]

                        // Create a new, remembered InteractionSource for each tile
                        val interactionSource = remember { MutableInteractionSource() }

                        SudokuTile(
                            tileData = tileData,
                            selectedTileIndices = selectedTileIndices,
                            interactionSource = interactionSource, // Pass the InteractionSource
                            onClick = { selectTile(row, col) }, // Pass the click action
                            modifier = Modifier.weight(1f),
                            selectedNumber = if (selectedTileIndices[0] != null) boardData.board[selectedTileIndices[0]!!][selectedTileIndices[1]!!].value else null
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SudokuBoardPreview() {
    val initialBoardValues = listOf(
        listOf(5, 3, null, null, 7, null, null, null, null),
        listOf(6, null, null, 1, 9, 5, null, null, null),
        listOf(null, 9, 8, null, null, null, null, 6, null),
        listOf(8, null, null, null, 6, null, null, null, 3),
        listOf(4, null, null, 8, null, 3, null, null, 1),
        listOf(7, null, null, null, 2, null, null, null, 6),
        listOf(null, 6, null, null, null, null, 2, 8, null),
        listOf(null, null, null, 4, 1, 9, null, null, 5),
        listOf(null, null, null, null, 8, null, null, 7, 9)
    )

    // Using the new secondary constructor
    val sudokuBoard = SudokuBoardData.fromInitialValues(initialBoardValues)

    // Optionally, fill sampleBoardData with some initial values for preview
    SudokuBoard(boardData = sudokuBoard)
}