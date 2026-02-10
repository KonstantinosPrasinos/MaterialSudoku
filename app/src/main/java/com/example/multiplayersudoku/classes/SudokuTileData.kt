package com.example.multiplayersudoku.classes

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SudokuTileData(
    var value: Int? = null, // null if the tile is empty
    var rowIndex: Int? = null,
    var colIndex: Int? = null,
    var isEditable: Boolean = true,
    var isSelected: Boolean = false,
    var isMistake: Boolean = false,
    var notes: MutableList<Int> = mutableListOf(),
    var isCompleted: Boolean = false
) {
    // Required no-argument constructor
    constructor() : this(value = null, isEditable = true)
}
