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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignInModal(onDismissRequest: () -> Unit, sheetState: SheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clientId = context.getString(R.string.FIREBASE_SERVER_CLIENT_ID)

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

                Firebase.auth.signInWithCredential(authCredential)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = Firebase.auth.currentUser
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                        }
                    }

                Log.d(TAG, "Firebase sign-in success: ${authResult.user?.uid}")

                // Close the bottom sheet on success
                onDismissRequest()
            } catch (e: Exception) {
                Log.e(TAG, "Firebase sign-in failed", e)
            }
        } else {
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
        } catch (e: Exception) {
            Log.e(TAG, "handleNoCredentialException: ", e)
        }
    }

    fun onGoogleSignInClick(activity: Activity) {
        scope.launch {
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
                handleNoCredentialException(activity)
            } catch (e: GetCredentialException) {
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

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Sign in to unlock more features",
                style = MaterialTheme.typography.headlineSmallEmphasized,
                color = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "By signing in you unlock: ",
                    style = MaterialTheme.typography.bodyLargeEmphasized
                )
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
                    Text("Multiplayer", style = MaterialTheme.typography.bodyLargeEmphasized)
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
                    Text("Statistics back up", style = MaterialTheme.typography.bodyLargeEmphasized)
                }
            }
            OutlinedButton(
                onClick = { loginWithGoogle() },
                shapes = ButtonDefaults.shapes()
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