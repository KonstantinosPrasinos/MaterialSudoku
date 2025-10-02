package com.example.multiplayersudoku.classes

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
    }
}

