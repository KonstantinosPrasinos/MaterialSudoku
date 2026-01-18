package com.example.multiplayersudoku.views.lobbyView

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.multiplayersudoku.classes.RoomData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import generateRandomNumber
import kotlinx.coroutines.tasks.await
import padWithZeros
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
) : ViewModel() {
    private lateinit var database: DatabaseReference
    private lateinit var firestoreDatabase: FirebaseFirestore

    var currentUser: FirebaseUser? by mutableStateOf(null)
        private set

    var roomData: RoomData? by mutableStateOf<RoomData?>(null)
        private set

    suspend fun init(lobbyArgs: LobbyArgs) {
        database = Firebase.database.reference
        firestoreDatabase = Firebase.firestore

        if (lobbyArgs.roomCode.isNotEmpty()) {
            // Join the room
            return
        }

        var roomCodeString: String

        do {
            val randomCode = generateRandomNumber(0, 9999)
            roomCodeString = padWithZeros(randomCode, 4)

            val result = database.child("rooms").child(roomCodeString).get().await()

            if (result != null && result.value != null) {
                // Room code in use
                roomCodeString = ""
            }

            // Create the room
        } while (roomCodeString.isEmpty())

        currentUser = Firebase.auth.currentUser

        if (currentUser == null) {
            return
        }

        // Create the room
        val roomRef = database.child("rooms").child(roomCodeString)
        val tempRoomData =
            RoomData(gameSettings = lobbyArgs.gameSettings, roomCode = roomCodeString, ownerPath = currentUser!!.uid)

        database.child("rooms").child(roomCodeString).setValue(tempRoomData).await()

        // Add reference
        val roomListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                roomData = dataSnapshot.getValue(RoomData::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        roomRef.addValueEventListener(roomListener)
    }
}