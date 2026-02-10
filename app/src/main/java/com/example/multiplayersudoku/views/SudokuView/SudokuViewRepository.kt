package com.example.multiplayersudoku.views.SudokuView

import com.example.multiplayersudoku.classes.RoomData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.firestore.FirebaseFirestore
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

    suspend fun setOwnerBoard(roomData: RoomData) {
        rtdb
            .child("rooms")
            .child(roomData.roomCode).child("opponentPath")
            .setValue(roomData.flatOwnerBoard)
            .await()
    }
}