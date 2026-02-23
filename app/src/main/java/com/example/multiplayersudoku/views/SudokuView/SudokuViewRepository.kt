package com.example.multiplayersudoku.views.SudokuView

import Player
import com.example.multiplayersudoku.classes.RoomData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SudokuViewRepository @Inject constructor(
    private val rtdb: DatabaseReference,
    private val firestore: FirebaseFirestore
) {
    suspend fun getRoomData(roomCode: String, userId: String): RoomData {
        val roomSnapshot = rtdb.child("rooms").child(roomCode).get().await()

        val roomData: RoomData =
            roomSnapshot.getValue(RoomData::class.java) ?: throw Exception("Room data is null")

        return roomData
    }

    fun setAllBoards(roomData: RoomData) {
        val roomRef = FirebaseDatabase.getInstance().getReference("rooms/${roomData.roomCode}")

        roomRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                // 1. Get current room data
                val currentRoom = mutableData.getValue(RoomData::class.java)
                    ?: return Transaction.success(mutableData) // Room doesn't exist

                // 2. Update the fields
                // We use the 'ownerBoard' setter logic to automatically update flatOwnerBoard
                currentRoom.canonBoard = roomData.canonBoard
                currentRoom.ownerBoard = roomData.ownerBoard
                currentRoom.opponentBoard = roomData.opponentBoard

                // 3. Set the updated object back to mutableData
                mutableData.value = currentRoom

                return Transaction.success(mutableData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                snapshot: DataSnapshot?
            ) {
                if (committed) {
                    println("Boards initialized successfully!")
                } else {
                    println("Transaction failed: ${error?.message}")
                }
            }
        })
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

    suspend fun setOpponentBoard(roomData: RoomData) {
        val roomRef = FirebaseDatabase.getInstance().getReference("rooms/${roomData.roomCode}")

        val updates = mapOf(
            "flatOpponentBoard" to roomData.flatOpponentBoard,
            "opponentBoardPercentage" to roomData.opponentBoardPercentage
        )
        roomRef.updateChildren(updates).await()
    }

    suspend fun setOwnerBoard(roomData: RoomData) {
        val roomRef = FirebaseDatabase.getInstance().getReference("rooms/${roomData.roomCode}")

        val updates = mapOf(
            "flatOwnerBoard" to roomData.flatOwnerBoard,
            "ownerBoardPercentage" to roomData.ownerBoardPercentage
        )
        roomRef.updateChildren(updates).await()
    }
}