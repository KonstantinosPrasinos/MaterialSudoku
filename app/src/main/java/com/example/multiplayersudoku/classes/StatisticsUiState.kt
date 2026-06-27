package com.example.multiplayersudoku.classes

data class StatisticsUiState(
    val averageDuration: Long? = null,
    val bestTime: Long? = null,
    val totalGames: Int = 0,
    val completedGames: Int = 0,
    val totalDuration: Long? = null,
    val winStreak: Int = 0,
    val isLoading: Boolean = true
)