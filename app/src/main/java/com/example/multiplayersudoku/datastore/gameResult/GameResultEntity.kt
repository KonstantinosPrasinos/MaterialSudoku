package com.example.multiplayersudoku.datastore.gameResult

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference
import java.util.UUID

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val difficulty: String,
    val durationSeconds: Long,
    val wasCompleted: Boolean,
    val mistakesCount: Int,
    val hintsUsed: Int,
    val timestamp: Long = System.currentTimeMillis(),

    // --- Multiplayer Columns ---
    val isMultiplayer: Boolean = false,
    val opponentName: String? = null,
    val wonAgainstOpponent: Boolean? = null,

    // --- For Firebase
    var userRef: DocumentReference? = null,
    var opponentRef: DocumentReference? = null
)