package com.example.multiplayersudoku.datastore

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.multiplayersudoku.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class AuthRepository(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun loginWithGoogle(): FirebaseUser? {
        val clientId = context.getString(R.string.FIREBASE_SERVER_CLIENT_ID)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setFilterByAuthorizedAccounts(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)
        // ... handling the result and Firebase sign-in as shown before
        return Firebase.auth.currentUser
    }
}