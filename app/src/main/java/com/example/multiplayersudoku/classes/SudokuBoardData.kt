package com.example.multiplayersudoku.classes

import com.example.multiplayersudoku.utils.generateBoard

data class SudokuBoardData(
    val board: List<List<SudokuTileData>>, // A 2D list representing the 9x9 grid
    val solution: List<List<Int>> = emptyList() // A 2D list representing the complete solution
) {
    companion object {
        fun fromInitialValues(initialValues: List<List<Int?>>, solution: List<List<Int>> = emptyList()): SudokuBoardData {
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
            return SudokuBoardData(boardData, solution)
        }

        suspend fun generateRandom(difficulty: Difficulty = Difficulty.EASY): SudokuBoardData {
            return generateBoard(difficulty)
        }

        fun generateEmpty(): SudokuBoardData {
            val board = List(9) { row ->
                List(9) { column ->
                    SudokuTileData(
                        value = null,
                        isEditable = false, // Or true, depending on your logic
                        rowIndex = row,
                        colIndex = column
                    )
                }
            }

            return SudokuBoardData(board)
        }
    }

    fun copyBoard(): List<List<SudokuTileData>> {
        return board.map { row ->
            row.map { tile ->
                tile.copy(notes = tile.notes.toMutableList())
            }
        }
    }

    fun isMistake(row: Int, col: Int, number: Int): Boolean {
        if (solution.isEmpty() || row !in solution.indices || col !in solution[row].indices) {
            return false
        }
        return solution[row][col] != number
    }

    fun isEmpty(): Boolean {
        for (row in board) {
            for (tile in row) {
                if (tile.value != null) {
                    return false
                }
            }
        }
        return true
    }

    fun getAsNumberList(): List<List<Int>> {
        return board.map { row -> row.map({ it.value ?: 0 }) }
    }
}

