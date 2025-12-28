package com.example.multiplayersudoku.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.multiplayersudoku.R
import com.example.multiplayersudoku.datastore.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignInModal(onDismissRequest: () -> Unit, sheetState: SheetState) {
    val authRepository = AuthRepository(LocalContext.current)
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    fun loginWithGoogle() {
        scope.launch {
            isLoading = true
            authRepository.loginWithGoogle()
            isLoading = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ) {
        if (isLoading) {
            ContainedLoadingIndicator()
        }

        OutlinedButton(
            onClick = { loginWithGoogle() },
            shapes = ButtonDefaults.shapes()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google__g__logo),
                contentDescription = "Google Icon",
            )
            Text("Sign in with Google")
        }
    }
}