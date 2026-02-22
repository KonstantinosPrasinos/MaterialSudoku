package com.example.multiplayersudoku.views.SudokuView

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multiplayersudoku.R
import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.ui.theme.extendedColors
import com.example.multiplayersudoku.utils.formatDifficulty
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EndDialogText(difficulty: Difficulty, seconds: Int, userHasWon: Boolean) {
    val firstPart: String = when {
        userHasWon && difficulty === Difficulty.EASY -> "Mastered the basics! You conquered "
        userHasWon && difficulty === Difficulty.MEDIUM -> "Impressive focus! You navigated the twists of "
        userHasWon && difficulty === Difficulty.HARD -> "Absolute logic! You dismantled a "
        !userHasWon && difficulty === Difficulty.EASY -> "You may have lost on "
        !userHasWon && difficulty === Difficulty.MEDIUM -> ""
        else -> "No shame in falling here. "
    }

    val secondPart: String = formatDifficulty(difficulty)

    val thirdPart: String = when {
        userHasWon && difficulty === Difficulty.EASY -> " mode in just "
        userHasWon && difficulty === Difficulty.MEDIUM -> " difficulty in "
        userHasWon && difficulty === Difficulty.HARD -> " board in "
        !userHasWon && difficulty === Difficulty.EASY -> " mode, but at least it only took you "
        !userHasWon && difficulty === Difficulty.MEDIUM -> " difficulty proved to be quite a puzzle. You fought a good fight for "
        else -> " mode is a gauntlet, and you survived for "
    }

    val fourthPart: String = formatTime(seconds)

    val fifthPart: String = when {
        userHasWon && difficulty === Difficulty.EASY -> ". Ready to step up your game?"
        userHasWon && difficulty === Difficulty.MEDIUM -> ". You're officially a Sudoku contender."
        userHasWon && difficulty === Difficulty.HARD -> ". That's a performance even the pros would respect."
        !userHasWon && difficulty === Difficulty.EASY -> " to realize the numbers weren't on your side today."
        !userHasWon && difficulty === Difficulty.MEDIUM -> " , but the opponent was just one step ahead."
        else -> " before the grid finally got the best of you."
    }

    val emphasizedStyle =
        MaterialTheme.typography.titleMediumEmphasized.copy(color = MaterialTheme.colorScheme.primary)
            .toSpanStyle()
    val normalStyle =
        MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface)
            .toSpanStyle()

    Text(text = buildAnnotatedString {
        withStyle(
            style = normalStyle
        ) {
            append(firstPart)
        }

        // Apply a bold, red style to the difficulty
        withStyle(
            style = emphasizedStyle
        ) {
            append(secondPart)
        }

        withStyle(
            style = normalStyle
        ) {
            append(thirdPart)
        }

        // Apply an italic style to the time
        withStyle(
            style = emphasizedStyle
        ) {
            append(fourthPart)
        }

        withStyle(
            style = normalStyle
        ) {
            append(fifthPart)
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SudokuView(onBack: () -> Unit, gameSettings: GameSettings, roomCode: String?) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val viewModel: SudokuViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.init(gameSettings, roomCode)
        }
    }

    BackHandler(enabled = !viewModel.showExitDialog) {
        viewModel.setExitDialogVisibility(true)
    }

    val windowInfo = LocalWindowInfo.current
    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isFocused ->
            if (viewModel.isFirstRun) {
                viewModel.updateFirstRunState(false)
                return@collect
            }

            if (!isFocused) {
                viewModel.updatePauseState(true)
            }
        }
    }

    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularWavyProgressIndicator()
        }
    } else {
        if (viewModel.showExitDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setExitDialogVisibility(false) },
                title = { Text("Quit Game?") },
                text = { Text("Are you sure you want to leave? Your progress will be lost.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateShowExitDialogState(false)
                            onBack()
                        }
                    ) {
                        Text("Quit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setExitDialogVisibility(false) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (viewModel.showGameEndDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(if (viewModel.userHasWon) "You won 🎉" else "You lost 😅") },
                icon = {
                    if (viewModel.userHasWon) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chess_queen),
                            contentDescription = "Crown",
                            tint = MaterialTheme.extendedColors.win,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                },
                text = {
                    EndDialogText(
                        gameSettings.difficulty,
                        viewModel.seconds,
                        viewModel.userHasWon
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateShowGameEndDialogState(false)
                            onBack()
                        }
                    ) {
                        Text("Continue")
                    }
                }
            )
        }

        if (viewModel.showGameEndDialog) {
            EndDialogText(gameSettings.difficulty, viewModel.seconds, viewModel.userHasWon)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SudokuTopAppBar(
                    scrollBehavior = scrollBehavior,
                    isPaused = viewModel.isPaused,
                    togglePaused = viewModel::togglePaused,
                    onBack = { viewModel.setExitDialogVisibility(true) },
                    difficulty = gameSettings.difficulty,
                    seconds = viewModel.seconds
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoBar(
                    hints = viewModel.hints,
                    mistakes = viewModel.mistakes,
                    maxHints = gameSettings.hints,
                    maxMistakes = gameSettings.mistakes,
                )
                MultiplayerProgressBar(
                    player1Percentage = viewModel.roomData?.ownerBoardPercentage ?: 0f,
                    player1PhotoUrl = viewModel.user?.photoUrl.toString(),
                    player2Percentage = viewModel.roomData?.opponentBoardPercentage ?: 0f
                )
                SudokuBoard(
                    boardData = viewModel.sudokuBoard,
                    selectedTileIndices = viewModel.selectedTileIndices,
                    selectTile = viewModel::selectTile,
                    isPaused = viewModel.isPaused,
                    togglePaused = viewModel::togglePaused
                )
                NumberButtons(
                    onNumberClick = viewModel::setTileValue,
                    isPaused = viewModel.isPaused
                )
                ActionButtons(
                    isWritingNotes = viewModel.isWritingNotes,
                    toggleEditing = viewModel::toggleWritingNotes,
                    eraseTile = viewModel::eraseSelectedTile,
                    undoLastAction = viewModel::undoLastAction,
                    canUndo = viewModel.undoableActions.isNotEmpty(),
                    generateHint = viewModel::generateHint,
                    canGenerateHint = viewModel.hints < gameSettings.hints
                )
            }
        }
    }
}