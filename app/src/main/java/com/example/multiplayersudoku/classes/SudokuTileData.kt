package com.example.multiplayersudoku.classes

data class SudokuTileData(
    val value: Int?, // null if the tile is empty
    val rowIndex: Int? = null,
    var colIndex: Int? = null,
    val isEditable: Boolean,
    val isSelected: Boolean = false,
    val notes: MutableList<Int> = mutableListOf<Int>(),
)
