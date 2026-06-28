package com.example.multiplayersudoku.views.lobbyView

import android.Manifest
import android.content.ClipData
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.components.GameSettingsBottomSheet
import com.example.multiplayersudoku.components.UserIcon
import com.example.multiplayersudoku.ui.theme.FredokaFamily
import kotlinx.coroutines.launch

class LobbyArgs(gameSettings: GameSettings, roomCode: String) {
    var gameSettings: GameSettings = gameSettings;
    val roomCode: String = roomCode;
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LobbyView(
    lobbyArgs: LobbyArgs,
    onBack: () -> Unit,
    viewModel: LobbyViewModel = hiltViewModel(),
    onNavigateToSudoku: (GameSettings, String) -> Unit
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val clipboardManager = LocalClipboard.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = !viewModel.showExitConfirmDialog) {
        viewModel.toggleExitDialogVisibility()
    }

    // Required permissions for Bluetooth advertising and connection (SDK 31+)
    val hostPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    // 2. Register the Permission Launcher Contract
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // Check if all requested permissions were approved by the user
        val allGranted = permissionsMap.values.all { it }

        if (allGranted) {
            viewModel.toggleAdvertising()
        } else {
            Toast.makeText(
                context,
                "Bluetooth permissions are required to host.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.init(lobbyArgs, onBack, onNavigateToSudoku)
    }

    fun copyCode() {
        scope.launch {
            clipboardManager.setClipEntry(
                ClipEntry(
                    ClipData.newPlainText(
                        "Room code",
                        viewModel.roomData?.roomCode
                    )
                )
            )
        }
    }

    fun confirmGameSettings() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                viewModel.run { viewModel.confirmGameSettings() }
            }
        }
    }

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
                        tooltip = { PlainTooltip { Text("Back") } },
                        state = rememberTooltipState(),
                    ) {
                        FilledTonalIconButton(onClick = viewModel::toggleExitDialogVisibility) {
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
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection)
                )
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserIcon(photoUrl = viewModel.owner?.profilePictureURL)
                Text("vs")
                Box {
                    UserIcon(photoUrl = viewModel.opponent?.profilePictureURL)
                    this@Column.AnimatedVisibility(
                        visible = viewModel.roomData?.opponentReady == true,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = 4.dp),
                        enter = expandIn(
                            // Expands from the bottom-left corner of the surface
                            expandFrom = Alignment.BottomStart,
                            // Start with a very small size
                            initialSize = { androidx.compose.ui.unit.IntSize(1, 1) }
                        ) + fadeIn(),
                        exit = shrinkOut() + fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(20.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            tonalElevation = 2.dp,
                            shadowElevation = 2.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Ready",
                                modifier = Modifier.padding(2.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("2")
                    Text("-")
                    Text("1")
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialShapes.Cookie12Sided.toShape(),
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = "Ready",
                                modifier = Modifier.padding(4.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text("Nearby play", style = MaterialTheme.typography.titleLargeEmphasized)
                        Spacer(Modifier.weight(1f))
                        Switch(
                            checked = viewModel.isAdvertising,
                            onCheckedChange = { checked ->
                                if (checked) permissionLauncher.launch(hostPermissions)
                                else viewModel.toggleAdvertising()
                            }
                        )
                    }
                    Text(
                        "Allow players on nearby devices to find and join your games via Bluetooth",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.67f)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    tonalElevation = 3.dp,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(10.dp),
                    ) {
//                        Text("Lobby room code", style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.67f)))
                        Text(
                            viewModel.roomData?.roomCode ?: "Whoops",
                            style = MaterialTheme.typography.displayMediumEmphasized.copy(
                                fontFamily = FredokaFamily,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                        Text("Share this code with your friend to play together", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.67f)))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledIconButton(
                                shapes = IconButtonDefaults.shapes(),
                                onClick = { copyCode() }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy room code"
                                )
                            }
                            FilledIconButton(
                                shapes = IconButtonDefaults.shapes(),
                                onClick = { }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Share room code"
                                )
                            }
                        }
                    }
                }
                Surface(
                    tonalElevation = 3.dp,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(10.dp),
                    ) {
                        Row {
                            Text("Difficulty: ", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${viewModel.roomData?.gameSettings?.difficultyName}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLargeEmphasized
                            )
                        }
                        Row {
                            Text("Mistakes: ", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${viewModel.roomData?.gameSettings?.mistakes}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLargeEmphasized
                            )
                        }
                        Row {
                            Text("Hints: ", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${viewModel.roomData?.gameSettings?.hints}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLargeEmphasized
                            )
                        }
                        FilledIconButton(
                            onClick = viewModel::toggleGameSettingsBottomSheetVisibility,
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit game properties"
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            if (viewModel.isOwner) {
                Button(
                    enabled = viewModel.roomData?.opponentReady == true,
                    onClick = viewModel::startGame,
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier
                        .heightIn(min = ButtonDefaults.MediumContainerHeight)
                        .fillMaxWidth()
                        .padding(10.dp)
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    contentPadding = ButtonDefaults.MediumContentPadding
                ) {
                    Text(
                        viewModel.startButtonText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            if (!viewModel.isOwner) {
                Button(
                    onClick = viewModel::toggleReady,
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier
                        .heightIn(min = ButtonDefaults.MediumContainerHeight)
                        .fillMaxWidth()
                        .padding(10.dp)
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    contentPadding = ButtonDefaults.MediumContentPadding
                ) {
                    Text(
                        if (viewModel.roomData?.opponentReady == true) "Unready" else "Ready",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        if (viewModel.showGameSettingsBottomSheet) {
            GameSettingsBottomSheet(
                sheetState = sheetState,
                toggleVisibility = viewModel::toggleGameSettingsBottomSheetVisibility,
                setDifficulty = viewModel::setGameDifficulty,
                selectedDifficulty = viewModel.roomData?.gameSettings?.difficulty
                    ?: GameSettings.defaultDifficulty,
                selectedMistakesOption = viewModel.roomData?.gameSettings?.mistakes.toString(),
                setSelectedMistakesOption = { viewModel.setMistakes(it.toInt()) },
                selectedHintsOption = viewModel.roomData?.gameSettings?.hints.toString(),
                setSelectedHintsOption = { viewModel.setHints(it.toInt()) },
                confirmButtonText = "Confirm",
                confirmAction = ::confirmGameSettings
            )
        }

        if (viewModel.showExitConfirmDialog) {
            AlertDialog(
                onDismissRequest = viewModel::toggleExitDialogVisibility,
                title = { Text("Leave room?") },
                text = { Text("Are you sure you want to leave this room?" + viewModel.exitMessage) },
                confirmButton = {
                    TextButton(onClick = viewModel::handleExit) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::toggleExitDialogVisibility) { Text("Cancel") }
                }
            )
        }
    }
}