package com.example.multiplayersudoku.views.mainView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiplayersudoku.datastore.FirebaseAuthRepository
import com.example.multiplayersudoku.datastore.gameResult.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    lateinit var onNavigateToJoinRoom: () -> Unit

    var showLoginModal by mutableStateOf(false)
        private set

    // Hot flow: UI always gets the latest user instantly
    val currentUser = repository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun init(onNavigateToJoinRoom: () -> Unit) {
        this.onNavigateToJoinRoom = onNavigateToJoinRoom

        viewModelScope.launch(Dispatchers.IO) {
            try {
                statisticsRepository.syncResultsToAndFromFirestore()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun navigateToJoinRoom() {
        if (currentUser.value != null) {
            this.onNavigateToJoinRoom()
            return;
        }

        this.showLoginModal = true;
    }

    fun closeLoginModal() {
        this.showLoginModal = false;
    }
}