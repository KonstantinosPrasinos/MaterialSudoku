package com.example.multiplayersudoku.views.joinRoomView

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.services.BleService
import com.example.multiplayersudoku.views.lobbyView.LobbyArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class NearbyGame(
    val roomCode: String,
    val deviceAddress: String,
    val hostUsername: String
)

@HiltViewModel
class JoinRoomViewModel @Inject constructor(
    private val bleService: BleService,
    private val firestore: FirebaseFirestore,
    private val database: DatabaseReference
) : ViewModel() {

    val isBleSupported: Boolean = bleService.hasBle

    var isScanning by mutableStateOf(false)
        private set

    var showNearbyScanning by mutableStateOf(true)
        private set

    var nearbyGames by mutableStateOf<List<NearbyGame>>(emptyList())
        private set

    private val activeListeners = mutableMapOf<String, ValueEventListener>()

    val roomCodeState = TextFieldState()

    var selectedDifficulty by mutableStateOf(GameSettings.defaultDifficulty)
        private set

    var roomCodeError by mutableStateOf("")
        private set

    lateinit var onNavigateToLobby: (LobbyArgs) -> Unit

    fun init(onNavigateToLobby: (LobbyArgs) -> Unit) {
        // Check for nearby scanning permissions

        this.onNavigateToLobby = onNavigateToLobby
        this.startScanning()
    }

    fun attemptJoinRoom() {
        viewModelScope.launch {
            try {
                val result = database.child("rooms").child(roomCodeState.text.toString()).get().await()

                if (result == null || result.value == null) {
                    roomCodeError = "Room not found"
                    return@launch
                }

                if (result.child("opponentPath").value != null) {
                    roomCodeError = "Room is full"
                    return@launch
                }

                val roomCode = roomCodeState.text.toString()

                onNavigateToLobby(LobbyArgs(GameSettings(), roomCode))
            } catch (e: Exception) {
                Log.e("SudokuApp", "Failed to sync game: ${e.message}", e)
                roomCodeError = "Failed to join game"
            }
        }
    }

    fun createRoom() {
        val gameSettings = GameSettings()
        gameSettings.difficulty = selectedDifficulty

        onNavigateToLobby(LobbyArgs(gameSettings, ""))
    }

    fun setSelectedDifficultyState(difficulty: Difficulty) {
        selectedDifficulty = difficulty
    }

    fun resetRoomCodeError() {
        roomCodeError = ""
    }

    fun clearTextField() {
        roomCodeState.clearText()
    }

    fun toggleScanning() {
        if (!isBleSupported) return

        if (isScanning) {
            stopScanning()
        } else {
            startScanning()
        }
    }

    fun startScanning() {
        if (!isBleSupported || isScanning) return
        isScanning = true
        nearbyGames = emptyList()

        bleService.startScanningForLobbies(
            onDiscovered = { roomCode, address ->
                if (!activeListeners.containsKey(roomCode)) {
                    setupRoomListener(roomCode, address)
                }
            },
            onLost = { roomCode, address ->
                removeRoomListener(roomCode)
                nearbyGames = nearbyGames.filter { it.roomCode != roomCode }
            }
        )
    }

    fun stopScanning() {
        if (!isScanning) return
        isScanning = false
        bleService.stopScanningForLobbies()

        val roomCodes = activeListeners.keys.toList()
        for (roomCode in roomCodes) {
            removeRoomListener(roomCode)
        }
        nearbyGames = emptyList()
    }

    fun setNearbyScanning(value: Boolean = false) {
        showNearbyScanning = value
    }

    private fun setupRoomListener(roomCode: String, address: String) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists = snapshot.exists()
                val opponentPath = snapshot.child("opponentPath").value
                val isFull = opponentPath != null

                if (!exists || isFull) {
                    nearbyGames = nearbyGames.filter { it.roomCode != roomCode }
                } else {
                    val ownerPath = snapshot.child("ownerPath").value as? String
                    if (ownerPath != null) {
                        viewModelScope.launch {
                            try {
                                val doc = firestore.collection("users").document(ownerPath).get().await()
                                val displayName = doc.getString("displayName") ?: "Unknown Host"
                                val newGame = NearbyGame(roomCode, address, displayName)
                                nearbyGames = nearbyGames.filter { it.roomCode != roomCode } + newGame
                            } catch (e: Exception) {
                                Log.e("JoinRoomViewModel", "Failed to fetch user data: ${e.message}", e)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoinRoomViewModel", "Database listener cancelled: ${error.message}")
            }
        }

        database.child("rooms").child(roomCode).addValueEventListener(listener)
        activeListeners[roomCode] = listener
    }

    private fun removeRoomListener(roomCode: String) {
        val listener = activeListeners.remove(roomCode)
        if (listener != null) {
            database.child("rooms").child(roomCode).removeEventListener(listener)
        }
    }

    fun joinNearbyGame(game: NearbyGame) {
        viewModelScope.launch {
            try {
                val result = database.child("rooms").child(game.roomCode).get().await()

                if (result == null || result.value == null) {
                    roomCodeError = "Room not found"
                    return@launch
                }

                if (result.child("opponentPath").value != null) {
                    roomCodeError = "Room is full"
                    return@launch
                }

                onNavigateToLobby(LobbyArgs(GameSettings(), game.roomCode))
            } catch (e: Exception) {
                Log.e("JoinRoomViewModel", "Failed to join game: ${e.message}", e)
                roomCodeError = "Failed to join game"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isScanning) {
            stopScanning()
        }
    }
}
