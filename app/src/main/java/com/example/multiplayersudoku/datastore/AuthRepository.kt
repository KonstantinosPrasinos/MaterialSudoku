package com.example.multiplayersudoku.datastore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    fun signOut()
}

// The concrete implementation
@Singleton
class FirebaseAuthRepository @Inject constructor() : AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    // Observes auth changes (Login/Logout) in real-time
    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun signOut() {
        auth.signOut()
    }
}