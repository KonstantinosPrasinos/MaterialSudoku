package com.example.multiplayersudoku.classes

import com.example.multiplayersudoku.utils.generateBoard

data class SudokuBoardData(
    val board: List<List<SudokuTileData>> // A 2D list representing the 9x9 grid
) {
    companion object {
        fun fromInitialValues(initialValues: List<List<Int?>>): SudokuBoardData {
            val boardData = initialValues.mapIndexed { rowIndex, rowList ->
                rowList.mapIndexed { colIndex, value ->
                    SudokuTileData(
                        value = value,
                        rowIndex = rowIndex,
                        colIndex = colIndex,
                        isEditable = (value == null),
                        isSelected = false
                    )
                }
            }
            return SudokuBoardData(boardData)
        }

        fun generateRandom(difficulty: Difficulty = Difficulty.EASY): SudokuBoardData {
            return SudokuBoardData(generateBoard(difficulty))
        }
    }

    fun getAsNumberList(): List<List<Int>> {
        return board.map {row -> row.map({it.value ?: 0})}
    }
}

