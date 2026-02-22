package com.example.multiplayersudoku.classes

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class RoomData(
    val gameSettings: GameSettings = GameSettings(),
    var roomCode: String = "",
    val ownerPath: String = "",
    var opponentPath: String? = null,
    var opponentReady: Boolean = false,
    var roomStateName: String = RoomState.LOBBY.name,
    var canonBoard: List<List<Int>>? = null,
    var flatOwnerBoard: List<SudokuTileData>? = null,
    var flatOpponentBoard: List<SudokuTileData>? = null,
    var opponentReadyToPlay: Boolean = false, // This is true when the opponent has initialized sudoku view to start the timer
    var ownerBoardPercentage: Float = 0.0f,
    var opponentBoardPercentage: Float = 0.0f
) {
    @get:Exclude
    @set:Exclude
    var roomState: RoomState
        get() = RoomState.valueOf(roomStateName)
        set(value) {
            roomStateName = value.name
        }

    // This is what your App/UI will use
    @get:Exclude
    @set:Exclude
    var ownerBoard: List<List<SudokuTileData>>?
        get() = flatOwnerBoard?.chunked(9)
        set(value) {
            flatOwnerBoard = value?.flatten()
        }

    @get:Exclude
    @set:Exclude
    var opponentBoard: List<List<SudokuTileData>>?
        get() = flatOpponentBoard?.chunked(9)
        set(value) {
            flatOpponentBoard = value?.flatten()
        }

    fun copy(
        gameSettings: GameSettings = this.gameSettings,
        roomCode: String = this.roomCode,
        ownerPath: String = this.ownerPath,
        opponentPath: String? = this.opponentPath,
        opponentReady: Boolean = this.opponentReady,
        roomState: RoomState = this.roomState,
        canonBoard: List<List<Int>>? = this.canonBoard,
        ownerBoard: List<List<SudokuTileData>>? = this.ownerBoard,
        opponentBoard: List<List<SudokuTileData>>? = this.opponentBoard,
        opponentReadyToPlay: Boolean = this.opponentReadyToPlay,
        ownerBoardPercentage: Float = this.ownerBoardPercentage,
        opponentBoardPercentage: Float = this.opponentBoardPercentage
    ): RoomData {
        val newRoom = RoomData(
            gameSettings, roomCode, ownerPath, opponentPath,
            opponentReady, roomState.name, canonBoard,
            null, null, opponentReadyToPlay, ownerBoardPercentage, opponentBoardPercentage
        )
        newRoom.ownerBoard = ownerBoard
        newRoom.opponentBoard = opponentBoard
        return newRoom
    }
}