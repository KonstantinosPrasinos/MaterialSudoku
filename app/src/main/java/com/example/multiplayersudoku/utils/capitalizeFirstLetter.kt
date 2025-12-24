package com.example.multiplayersudoku.utils

fun capitalizeFirstLetter(text: String): String {
    return text.lowercase().replaceFirstChar { it.uppercase() }
}