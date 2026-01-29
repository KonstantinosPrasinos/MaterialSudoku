package com.example.multiplayersudoku.views.lobbyView

import Player
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.classes.RoomData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val repository: LobbyRepository
) : ViewModel() {
    private lateinit var database: DatabaseReference
    private lateinit var firestoreDatabase: FirebaseFirestore

    var currentUser: FirebaseUser? by mutableStateOf(null)
        private set

    var showGameSettingsBottomSheet: Boolean by mutableStateOf(false)
        private set

    var roomData: RoomData? by mutableStateOf(null)
        private set

    var owner: Player? by mutableStateOf(null)
        private set

    var opponent: Player? by mutableStateOf(null)
        private set

    fun setGameDifficulty(difficulty: Difficulty) {
        roomData = roomData?.copy(
            gameSettings = roomData?.gameSettings?.copy(difficulty = difficulty) ?: return
        )
    }

    fun setMistakes(mistakes: Int) {
        roomData = roomData?.copy(
            gameSettings = roomData?.gameSettings?.copy(mistakes = mistakes) ?: return
        )
    }

    fun setHints(hints: Int) {
        roomData = roomData?.copy(
            gameSettings = roomData?.gameSettings?.copy(hints = hints) ?: return
        )
    }

    suspend fun confirmGameSettings() {
        toggleGameSettingsBottomSheetVisibility()
        repository.updateGameSettings(roomData?.roomCode ?: "", roomData?.gameSettings ?: return)
    }

    suspend fun initializePlayers() {
        if (roomData == null) {
            Log.e(TAG, "Room data is null ${roomData?.ownerPath}")
            return
        }

        if (roomData?.ownerPath == null) {
            Log.e(TAG, "Owner path is null")
            return
        }

        owner = repository.getPlayerData(roomData?.ownerPath ?: "")
        if (roomData?.ownerPath != null) {
            opponent = repository.getPlayerData(roomData?.opponentPath ?: "")
        }
    }

    suspend fun init(lobbyArgs: LobbyArgs) {
        var code: String;

        if (lobbyArgs.roomCode.isEmpty()) {
            // The user is the owner so initialize the game
            code = repository.generateUniqueRoomCode()
            val user = Firebase.auth.currentUser ?: return
            val newRoom = RoomData(lobbyArgs.gameSettings, code, user.uid)
            repository.createRoom(newRoom)
        } else {
            // Queue the code to join the game
            code = lobbyArgs.roomCode
            roomData = repository.joinRoom(code, Firebase.auth.currentUser?.uid ?: "")
        }

        // Start observing room changes
        viewModelScope.launch {
            repository.observeRoom(code).collect { updatedRoom ->
                roomData = updatedRoom
                if (owner == null) fetchOwner(updatedRoom.ownerPath)
            }
        }

        initializePlayers()
    }

    private suspend fun fetchOwner(ownerId: String) {
        owner = repository.getPlayerData(ownerId)
    }

    fun toggleGameSettingsBottomSheetVisibility() {
        showGameSettingsBottomSheet = !showGameSettingsBottomSheet
    }
}