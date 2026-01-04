package com.example.multiplayersudoku.utils

import com.example.multiplayersudoku.classes.Difficulty

fun getDifficultyFromString(difficultyStr: String): Difficulty {
    val difficulty = when (difficultyStr) {
        "EASY" -> Difficulty.EASY
        "MEDIUM" -> Difficulty.MEDIUM
        "HARD" -> Difficulty.HARD
        else -> throw IllegalArgumentException("Invalid difficulty string: $difficultyStr")
    }
    
    return difficulty
}