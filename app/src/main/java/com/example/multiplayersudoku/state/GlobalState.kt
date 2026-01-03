package com.example.multiplayersudoku.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// The data model
data class User(val displayName: String, val photoUrl: String?)

// The holder class containing the data AND the setter
class GlobalStateContext {
    var user by mutableStateOf<User?>(null)
        private set // External read-only

    fun updateUser(newUser: User?) {
        user = newUser
    }
}

// The CompositionLocal provider with a dummy default
val GlobalState = staticCompositionLocalOf<GlobalStateContext> {
    error("No UserContext provided")
}