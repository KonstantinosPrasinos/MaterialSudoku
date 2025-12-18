package com.example.multiplayersudoku.classes

class GameSettings {
    var hints: Int = 0
    var mistakes: Int = 0
    var difficulty: Difficulty = Difficulty.EASY

    companion object {
        var maxHints: Int = 5
        var maxMistakes: Int = 3
        var defaultDifficulty: Difficulty = Difficulty.EASY
    }
}