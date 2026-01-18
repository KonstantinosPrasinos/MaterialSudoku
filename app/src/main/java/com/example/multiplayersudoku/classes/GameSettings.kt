package com.example.multiplayersudoku.classes

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class GameSettings(
    var hints: Int = 0,
    var mistakes: Int = 0,
    var difficultyName: String = Difficulty.EASY.name
) {
    @get:Exclude
    @set:Exclude
    var difficulty: Difficulty
        get() = Difficulty.valueOf(difficultyName)
        set(value) {
            difficultyName = value.name
        }

    companion object {
        var maxHints: Int = 5
        var maxMistakes: Int = 3
        var defaultDifficulty: Difficulty = Difficulty.EASY
    }

    constructor() : this(0, 0, Difficulty.EASY.name)
}