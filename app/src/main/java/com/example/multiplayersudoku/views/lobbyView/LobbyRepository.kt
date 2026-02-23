package com.example.multiplayersudoku.views.lobbyView

import Player
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.classes.RoomData
import com.example.multiplayersudoku.classes.RoomState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import generateRandomNumber
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import padWithZeros
import javax.inject.Inject

class LobbyRepository @Inject constructor(
    private val rtdb: DatabaseReference,
    private val firestore: FirebaseFirestore
) {
    suspend fun generateUniqueRoomCode(): String {
        var roomCodeString: String

        do {
            val randomCode = generateRandomNumber(0, 9999)
            roomCodeString = padWithZeros(randomCode, 4)

            val result = rtdb.child("rooms").child(roomCodeString).get().await()

            if (result != null && result.value != null) {
                roomCodeString = ""
            }

            // Create the room
        } while (roomCodeString.isEmpty())

        return roomCodeString
    }

    suspend fun getPlayerData(userId: String): Player? {
        if (userId.isEmpty()) return null

        val document = firestore.collection("users").document(userId).get().await()
        return if (document.exists()) {
            Player(
                displayName = document.getString("displayName") ?: "",
                profilePictureURL = document.getString("photoUrl") ?: "",
                id = userId
            )
        } else null
    }

    suspend fun updateGameSettings(roomCode: String, gameSettings: GameSettings) {
        rtdb.child("rooms").child(roomCode).child("gameSettings").setValue(gameSettings).await()
    }

    suspend fun createRoom(roomData: RoomData) {
        rtdb.child("rooms").child(roomData.roomCode).setValue(roomData).await()
    }

    suspend fun joinRoom(roomCode: String, userId: String): RoomData {
        val room = rtdb.child("rooms").child(roomCode)
        room.child("opponentPath").setValue(userId).await()

        val roomSnapshot = room.get().await();

        val roomData: RoomData =
            roomSnapshot.getValue(RoomData::class.java) ?: throw Exception("Room data is null")

        return roomData
    }

    fun observeRoom(roomCode: String) = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(RoomData::class.java)?.let { trySend(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        rtdb.child("rooms").child(roomCode).addValueEventListener(listener)
        awaitClose { rtdb.child("rooms").child(roomCode).removeEventListener(listener) }
    }

    suspend fun leaveRoom(roomData: RoomData) {
        rtdb.child("rooms").child(roomData.roomCode).child("opponentPath").setValue(null).await()
    }

    suspend fun deleteRoom(roomData: RoomData) {
        rtdb.child("rooms").child(roomData.roomCode).removeValue().await()
    }

    suspend fun setOpponentReady(roomCode: String, opponentReady: Boolean) {
        rtdb.child("rooms").child(roomCode).child("opponentReady").setValue(opponentReady).await()
    }

    suspend fun startGame(roomCode: String) {
        rtdb
            .child("rooms")
            .child(roomCode).child("roomStateName")
            .setValue(RoomState.PLAYING.name)
            .await()
    }
}