package com.example.multiplayersudoku.classes

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class RoomData(
    val gameSettings: GameSettings = GameSettings(),
    val roomCode: String = "",
    val ownerPath: String = "",
    var opponentPath: String? = null
)