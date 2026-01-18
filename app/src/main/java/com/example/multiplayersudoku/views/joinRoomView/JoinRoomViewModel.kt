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
import com.example.multiplayersudoku.views.lobbyView.LobbyArgs
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class JoinRoomViewModel @Inject constructor(
) : ViewModel() {
    private var database: DatabaseReference = Firebase.database.reference

    val roomCodeState = TextFieldState()

    var selectedDifficulty by mutableStateOf(GameSettings.defaultDifficulty)
        private set

    var roomCodeError by mutableStateOf("")
        private set

    lateinit var onNavigateToLobby: (LobbyArgs) -> Unit

    fun init(onNavigateToLobby: (LobbyArgs) -> Unit) {
        this.onNavigateToLobby = onNavigateToLobby
    }

    fun attemptJoinRoom() {
        viewModelScope.launch {
            try {
                val result = database.child("rooms").child(roomCodeState.text.toString()).get().await()

                if (result == null || result.value == null) {
                    roomCodeError = "Room not found"
                    return@launch
                }

                onNavigateToLobby(LobbyArgs(GameSettings(), roomCodeState.text.toString()))
            } catch (e: Exception) {
                Log.e("SudokuApp", "Failed to sync game: ${e.message}", e)
                roomCodeError = "Failed to join game"
            }
        }
    }

    fun createRoom() {
        val gameSettings = GameSettings()
        gameSettings.difficulty = selectedDifficulty

        onNavigateToLobby(LobbyArgs(gameSettings, "whoops"))
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
}
