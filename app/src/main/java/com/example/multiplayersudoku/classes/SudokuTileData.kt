package com.example.multiplayersudoku.classes

data class SudokuTileData(
    var value: Int?, // null if the tile is empty
    val rowIndex: Int? = null,
    var colIndex: Int? = null,
    var isEditable: Boolean,
    val isSelected: Boolean = false,
    val isMistake: Boolean = false,
    var notes: MutableList<Int> = mutableListOf(),
)
