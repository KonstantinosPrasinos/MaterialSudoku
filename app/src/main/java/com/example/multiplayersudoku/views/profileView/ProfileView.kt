package com.example.multiplayersudoku.views.profileView

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multiplayersudoku.components.List.List
import com.example.multiplayersudoku.components.List.ListItem
import com.example.multiplayersudoku.components.List.ListItemOrder
import com.example.multiplayersudoku.components.SignInModal
import com.example.multiplayersudoku.components.UserIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    onBack: () -> Unit, onNavigateToStatistics: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    var showLoginModal by remember { mutableStateOf(false) }
    var showLogoutConfirmModal by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val user by viewModel.currentUser.collectAsState()

    fun toggleLoginBottomSheet() {
        if (showLoginModal) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showLoginModal = false
                }
            }
        } else {
            showLoginModal = true
        }
    }

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        TooltipBox(
                            positionProvider =
                                TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Above
                                ),
                            tooltip = { PlainTooltip { Text("Menu") } },
                            state = rememberTooltipState(),
                        ) {
                            FilledTonalIconButton(onClick = { onBack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                )
            }
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                UserIcon(
                    photoUrl = if (user != null) user?.photoUrl.toString() else null, modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "account-circle"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
                Text(
                    user?.displayName.toString(),
                    style = MaterialTheme.typography.headlineMediumEmphasized,
                    color = if (user != null) MaterialTheme.colorScheme.onSurface else Color.Transparent
                )
                List(padding = 10.dp) {
                    ListItem(
                        order = ListItemOrder.FIRST,
                        onClick = {
                            onNavigateToStatistics()
                        },
                        content = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Statistics", style = MaterialTheme.typography.bodyLargeEmphasized)
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Chevron right"
                                )
                            }
                        })
                    if (user == null) {
                        ListItem(order = ListItemOrder.LAST, onClick = {
                            toggleLoginBottomSheet()
                        }, content = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Login", style = MaterialTheme.typography.bodyLargeEmphasized)
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Login,
                                    contentDescription = "Log in icon"
                                )
                            }
                        })
                    }
                    if (user !== null) {
                        ListItem(order = ListItemOrder.LAST, onClick = {
                            showLogoutConfirmModal = true
                        }, content = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Log out", style = MaterialTheme.typography.bodyLargeEmphasized)
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Log out icon"
                                )
                            }
                        })
                    }
                    if (showLoginModal) {
                        SignInModal(onDismissRequest = { toggleLoginBottomSheet() }, sheetState, snackbarHostState)
                    }
                    if (showLogoutConfirmModal) {
                        AlertDialog(
                            onDismissRequest = { showLogoutConfirmModal = false },
                            title = { Text("Logout?") },
                            text = { Text("Are you sure you want to log out? This will keep your progress locally but you can reset it later") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showLogoutConfirmModal = false
                                    viewModel.signOut()
                                }) { Text("Confirm") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showLogoutConfirmModal = false }) { Text("Cancel") }
                            }
                        )
                    }
                }
            }
        }
    }
}