package com.example.multiplayersudoku.components

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.multiplayersudoku.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.res.stringResource
import com.example.multiplayersudoku.state.GlobalState
import com.example.multiplayersudoku.state.User
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignInModal(onDismissRequest: () -> Unit, sheetState: SheetState, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clientId = stringResource(R.string.FIREBASE_SERVER_CLIENT_ID)
    val globalState = GlobalState.current

    var isLoading by remember { mutableStateOf(false) }

    suspend fun initFirebaseUser() {
        val authUser = Firebase.auth.currentUser ?: return
        val db = Firebase.firestore
        val userCollection = db.collection("users")

        val docRef = userCollection.document(authUser.uid)

        try {
            val document = docRef.get().await()

            if (document.exists()) {
                val user = User(
                    displayName = document.getString("displayName") ?: "",
                    photoUrl = document.getString("photoUrl")
                )

                globalState.updateUser(user)
            } else {
                val newUser = User(
                    displayName = authUser.displayName ?: "",
                    photoUrl = authUser.photoUrl.toString()
                )

                try {
                    userCollection.document(authUser.uid).set(newUser).await()
                    globalState.updateUser(newUser)
                } catch (e: Exception) {
                    isLoading = false
                    Log.d(TAG, "Error adding user document", e)
                }
            }
        } catch (e: Exception) {
            isLoading = false
            Log.d(TAG, "Error getting user document", e)
        }
    }

    suspend fun firebaseAuthWithGoogle(result: androidx.credentials.GetCredentialResponse) {
        val credential = result.credential

        // Check if the credential is of the type Google ID Token
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // Extract the Google ID Token
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                // Exchange it for a Firebase Credential
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                // Sign in to Firebase
                val authResult = Firebase.auth.signInWithCredential(authCredential).await()

                initFirebaseUser()
                Log.d(TAG, "Firebase sign-in success")

                isLoading = false
                // Close the bottom sheet on success
                onDismissRequest()
            } catch (e: Exception) {
                isLoading = false
                snackbarHostState.showSnackbar("Failed to sign-in", withDismissAction = true)
                Log.e(TAG, "Firebase sign-in failed", e)
            }
        } else {
            isLoading = false
            snackbarHostState.showSnackbar("Failed to sign-in", withDismissAction = true)
            Log.e(TAG, "Unexpected credential type: ${credential.type}")
        }
    }

    suspend fun handleNoCredentialException(activity: Activity) {
        try {
            val signInWithGoogleOption = GetSignInWithGoogleOption
                .Builder(serverClientId = clientId)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()
            val result = CredentialManager.create(activity).getCredential(
                context = activity,
                request = request
            )
            firebaseAuthWithGoogle(result)
        } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
            // User cancelled the sign-up flow. Do nothing.
            isLoading = false
            Log.d(TAG, "User cancelled sign-up.")
        } catch (e: Exception) {
            isLoading = false
            snackbarHostState.showSnackbar("Failed to sign-in", withDismissAction = true)
            Log.e(TAG, "handleNoCredentialException: ", e)
        }
    }

    fun onGoogleSignInClick(activity: Activity) {
        scope.launch {
            isLoading = true
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(clientId)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            try {
                val result = CredentialManager.create(activity).getCredential(
                    context = activity,
                    request = request
                )
                firebaseAuthWithGoogle(result)
            } catch (e: NoCredentialException) {
                isLoading = false
                handleNoCredentialException(activity)
            } catch (e: GetCredentialException) {
                isLoading = false
                Log.i(TAG, "GetCredentialException: ", e)
            }
        }
    }

    fun loginWithGoogle() {
        scope.launch {
            try {
                onGoogleSignInClick(context as Activity)
            } catch (e: Exception) {
                Log.e("Auth", "Login failed", e)
            }
        }
    }

    fun handleDismissRequest() {
        if (isLoading) return
        onDismissRequest()
    }

    ModalBottomSheet(
        onDismissRequest = { handleDismissRequest() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                if (isLoading) {
                    CircularWavyProgressIndicator(modifier = Modifier.fillMaxSize())
                }
                Icon(
                    imageVector = Icons.Rounded.AccountCircle, // Or a lock/star icon
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Unlock Full Access",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to save your progress and compete.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp) // Sets the size to 10dp x 10dp
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialShapes.Cookie7Sided.toShape()
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Multiplayer Access",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp) // Sets the size to 10dp x 10dp
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialShapes.Cookie7Sided.toShape()
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Cloud Statistics Backup",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Button(
                enabled = !isLoading,
                contentPadding = ButtonDefaults.MediumContentPadding,
                onClick = { loginWithGoogle() },
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google__g__logo),
                        contentDescription = "Google Icon",
                    )
                    Text("Sign in with Google")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}