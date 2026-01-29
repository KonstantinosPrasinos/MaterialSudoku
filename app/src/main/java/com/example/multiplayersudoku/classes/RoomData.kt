package com.example.multiplayersudoku.classes

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class RoomData(
    val gameSettings: GameSettings = GameSettings(),
    var roomCode: String = "",
    val ownerPath: String = "",
    var opponentPath: String? = null,
    var opponentReady: Boolean = false
) {
    fun copy(
        gameSettings: GameSettings = this.gameSettings,
        roomCode: String = this.roomCode,
        ownerPath: String = this.ownerPath,
        opponentPath: String? = this.opponentPath,
        opponentReady: Boolean = this.opponentReady
    ): RoomData {
        return RoomData(gameSettings, roomCode, ownerPath, opponentPath, opponentReady)
    }
}