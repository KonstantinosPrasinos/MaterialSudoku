package com.example.multiplayersudoku.classes

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class GameSettings(
    var hints: Int = 1,
    var mistakes: Int = 1,
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

    fun copy(
        difficulty: Difficulty = this.difficulty,
        hints: Int = this.hints,
        mistakes: Int = this.mistakes
    ): GameSettings {
        return GameSettings(hints, mistakes, difficulty.name)
    }

    constructor() : this(1, 1, Difficulty.EASY.name)
}