package com.example.multiplayersudoku.utils

import com.example.multiplayersudoku.classes.Difficulty

fun formatDifficulty(difficulty: Difficulty): String {
    return difficulty.name.lowercase().replaceFirstChar { it.uppercase() }
}