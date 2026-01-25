package com.example.multiplayersudoku.classes

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class RoomData(
    val gameSettings: GameSettings = GameSettings(),
    var roomCode: String = "",
    val ownerPath: String = "",
    var opponentPath: String? = null
) {
    fun copy(
        gameSettings: GameSettings = this.gameSettings,
        roomCode: String = this.roomCode,
        ownerPath: String = this.ownerPath,
        opponentPath: String? = this.opponentPath
    ): RoomData {
        return RoomData(gameSettings, roomCode, ownerPath, opponentPath)
    }
}