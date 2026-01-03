package com.example.multiplayersudoku.views.profileView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiplayersudoku.datastore.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository
) : ViewModel() {

    // Hot flow: UI always gets the latest user instantly
    val currentUser = repository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun signOut() {
        repository.signOut()
    }
}