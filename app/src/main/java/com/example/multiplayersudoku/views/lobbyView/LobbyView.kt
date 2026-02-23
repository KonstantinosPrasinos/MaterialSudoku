package com.example.multiplayersudoku.views.lobbyView

import android.content.ClipData
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val layoutDirection = LocalLayoutDirection.current
    val clipboardManager = LocalClipboard.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.init(lobbyArgs, onBack, onNavigateToSudoku)
    }

    BackHandler(enabled = !viewModel.showExitConfirmDialog) {
        viewModel.toggleExitDialogVisibility()
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("2")
                Text("-")
                Text("1")
            }
            Spacer(Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                tonalElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(
                        onClick = { copyCode() },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text(
                            viewModel.roomData?.roomCode ?: "Whoops",
                            style = TextStyle(
                                fontFamily = FredokaFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 48.sp
                            )
                        )
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy room code",
                            Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Difficulty: ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${viewModel.roomData?.gameSettings?.difficultyName}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMediumEmphasized
                        )
                        Spacer(Modifier.weight(1f))
                        Text("Mistakes: ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${viewModel.roomData?.gameSettings?.mistakes}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMediumEmphasized
                        )
                        Spacer(Modifier.weight(1f))
                        Text("Hints: ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${viewModel.roomData?.gameSettings?.hints}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMediumEmphasized
                        )
                        Spacer(Modifier.weight(1f))
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (viewModel.isOwner) {
                            Button(
                                enabled = viewModel.roomData?.opponentReady == true,
                                onClick = viewModel::startGame,
                                shapes = ButtonDefaults.shapes(),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .heightIn(min = ButtonDefaults.MediumContainerHeight)
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
                                    .weight(1.5f)
                                    .heightIn(min = ButtonDefaults.MediumContainerHeight)
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