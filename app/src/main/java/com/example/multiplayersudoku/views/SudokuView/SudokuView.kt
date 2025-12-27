package com.example.multiplayersudoku.views.SudokuView

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.example.multiplayersudoku.classes.SudokuBoardData
import com.example.multiplayersudoku.classes.SudokuTileData
import com.example.multiplayersudoku.datastore.gameResult.GameResult
import com.example.multiplayersudoku.ui.theme.extendedColors
import com.example.multiplayersudoku.utils.attemptSolve
import com.example.multiplayersudoku.utils.checkCol
import com.example.multiplayersudoku.utils.checkGrid
import com.example.multiplayersudoku.utils.checkRow
import com.example.multiplayersudoku.utils.formatDifficulty
import com.example.multiplayersudoku.utils.updateNotes
import kotlinx.coroutines.delay

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
        MaterialTheme.typography.titleMediumEmphasized.copy(color = MaterialTheme.colorScheme.primary).toSpanStyle()
    val normalStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface)
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
fun SudokuView(onBack: () -> Unit, gameSettings: GameSettings) {
    val viewModel: SudokuViewModel = hiltViewModel()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showExitDialog by remember { mutableStateOf(false) }
    var showGameEndDialog by remember { mutableStateOf(false) }
    var userHasWon by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    var doTimer by remember { mutableStateOf(true) }

    var isPaused by remember { mutableStateOf(false) }
    var isWritingNotes by remember { mutableStateOf(false) }
    var sudokuBoard by remember {
        mutableStateOf(
            SudokuBoardData.generateRandom(gameSettings.difficulty)
        )
    }
    var selectedTileIndices by remember {
        mutableStateOf<List<Int?>>(
            listOf(
                null,
                null
            )
        )
    }
    val undoableActions = remember { mutableStateListOf<SudokuTileData>() }
    var hints by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }

    // This fires when isPaused is changed
    LaunchedEffect(isPaused) {
        while (!isPaused && doTimer) {
            delay(1000)
            seconds++
        }
    }

    /*
     This is used by [LaunchedEffect] to check if the window has lost focus
     */
    val windowInfo = LocalWindowInfo.current
    var isFirstRun by remember { mutableStateOf(true) }

    fun addToUndoableActions(action: SudokuTileData) = undoableActions.add(action)

    fun toggleGameEndDialog() {
        showGameEndDialog = !showGameEndDialog
    }

    fun generateHint() {
        if (hints >= gameSettings.hints) return
        if (selectedTileIndices[0] == null || selectedTileIndices[1] == null) return

        val row: Int = selectedTileIndices[0]!!
        val col: Int = selectedTileIndices[1]!!

        var tileToUpdate = sudokuBoard.board[row][col]

        if (!tileToUpdate.isEditable) return
        if (tileToUpdate.value != null && !tileToUpdate.isMistake) return

        hints += 1

        val newBoard = sudokuBoard.board.toMutableList().map { it.toMutableList() }
        tileToUpdate = newBoard[row][col]

        for (i in 1..9) {
            newBoard[row][col] = tileToUpdate.copy(value = i)

            val solvedBoard = attemptSolve(newBoard)

            if (solvedBoard.isSolved) {
                sudokuBoard = sudokuBoard.copy(
                    board = newBoard.map { it }
                )
                return
            }
        }
    }

    fun checkForMistakes(number: Int, row: Int, col: Int): Boolean {
        val newBoard = sudokuBoard.board.toMutableList().map { it.toMutableList() }
        val tileToUpdate = newBoard[row][col]

        val isDuplicate = checkRow(newBoard, tileToUpdate, number) ||
                checkCol(newBoard, tileToUpdate, number) ||
                checkGrid(newBoard, tileToUpdate, number)

        if (isDuplicate) return true

        // Place the value in the field and test
        newBoard[row][col] = tileToUpdate.copy(value = number)

        val solvedBoard = attemptSolve(newBoard)

        return !solvedBoard.isSolved
    }

    fun checkForWin(board: List<List<SudokuTileData>>): Boolean {
        var emptyTileFound = false

        for (row in board) {
            for (tile in row) {
                if (tile.value == null || tile.isMistake) {
                    emptyTileFound = true
                    break
                }
            }
        }

        return !emptyTileFound
    }

    fun eraseSelectedTile() {
        val row: Int? = selectedTileIndices[0]
        val col: Int? = selectedTileIndices[1]

        if (isPaused || row == null || col == null) return

        // Create a deep copy of the board to modify it
        val newBoard = sudokuBoard.board.toMutableList().map { it.toMutableList() }
        val tileToUpdate = newBoard[row][col]

        if (!tileToUpdate.isEditable) return

        // Add erasing to undoable actions
        addToUndoableActions(tileToUpdate)

        // Erase the notes
        val newNotes = mutableListOf<Int>()

        // Erase the value
        newBoard[row][col] = tileToUpdate.copy(value = null, notes = newNotes)

        sudokuBoard = sudokuBoard.copy(
            board = newBoard.map { it }
        )
    }

    fun undoTileState(prevTileState: SudokuTileData) {
        val row = prevTileState.rowIndex!!
        val col = prevTileState.colIndex!!

        val newBoard = sudokuBoard.board.toMutableList().map { it.toMutableList() }
        val tileToUpdate = newBoard[row][col]

        newBoard[row][col] =
            tileToUpdate.copy(value = prevTileState.value, notes = prevTileState.notes)

        sudokuBoard = sudokuBoard.copy(
            board = newBoard.map { it }
        )
    }

    fun finalizeGame() {
        doTimer = false

        val result = GameResult(
            difficulty = gameSettings.difficulty.name,
            durationSeconds = seconds.toLong(),
            wasCompleted = userHasWon,
            mistakesCount = mistakes,
            hintsUsed = hints
        )

        viewModel.onGameFinished(result)

        toggleGameEndDialog()
    }

    fun setTileValue(
        number: Int
    ) {
        val row: Int? = selectedTileIndices[0]
        val col: Int? = selectedTileIndices[1]

        if (isPaused || row == null || col == null) return

        // Create a deep copy of the board to modify it
        var newBoard = sudokuBoard.board.toMutableList().map { it.toMutableList() }
        val tileToUpdate = newBoard[row][col]

        // Check if the tile is editable before changing it
        if (!tileToUpdate.isEditable) return

        if (isWritingNotes) {
            // Logic for updating notes
            val newNotes =
                tileToUpdate.notes.toMutableList() // Use a set for notes to avoid duplicates
            if (newNotes.contains(number)) {
                newNotes.remove(number)
            } else {
                newNotes.add(number)
            }

            // Add note to undoable actions
            addToUndoableActions(tileToUpdate)

            newBoard[row][col] =
                tileToUpdate.copy(notes = newNotes)
        } else {
            if (number == tileToUpdate.value) {
                return
            }
            // Check if the set value is a mistake
            val isMistake = checkForMistakes(number, row, col)

            if (isMistake) {
                mistakes += 1
                if (mistakes >= gameSettings.mistakes) {
                    userHasWon = false
                    finalizeGame()
                }
            }

            newBoard[row][col] =
                tileToUpdate.copy(value = number, isMistake = isMistake)

            // Add setting value to undoable actions
            addToUndoableActions(tileToUpdate)
            newBoard =
                updateNotes(
                    newBoard,
                    tileToUpdate,
                    number
                ) as MutableList<MutableList<SudokuTileData>>

            if (checkForWin(newBoard)) {
                userHasWon = true
                finalizeGame()
            }
        }

        sudokuBoard = sudokuBoard.copy(
            board = newBoard.map { it }
        )
    }

    fun selectTile(row: Int?, col: Int?) {
        if (isPaused) return

        if (row == selectedTileIndices[0] && col == selectedTileIndices[1]) {
            selectedTileIndices = listOf(null, null)
        } else {
            selectedTileIndices = listOf(row, col)
        }
    }

    fun togglePaused() {
        selectTile(null, null)
        isPaused = !isPaused
    }

    fun setPaused(value: Boolean) {
        selectTile(null, null)
        isPaused = value
    }

    fun undoLastAction() {
        val solvedBoard = attemptSolve(sudokuBoard.board)

        sudokuBoard = sudokuBoard.copy(
            board = solvedBoard.boardData.map { it }
        )

        if (undoableActions.isEmpty()) return

        val lastAction = undoableActions.last()

        undoTileState(lastAction)
        undoableActions.removeAt(undoableActions.lastIndex)
    }

    fun setExitDialogVisibility(value: Boolean = true) {
        showExitDialog = value
        setPaused(value)
    }

    BackHandler(enabled = !showExitDialog) {
        setExitDialogVisibility(true)
    }

    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isFocused ->
            if (isFirstRun) {
                isFirstRun = false
                return@collect
            }

            if (!isFocused) {
                setPaused(true)
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { setExitDialogVisibility(false) },
            title = { Text("Quit Game?") },
            text = { Text("Are you sure you want to leave? Your progress will be lost.") },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onBack()
                    }
                ) {
                    Text("Quit")
                }
            },
            dismissButton = {
                TextButton(onClick = { setExitDialogVisibility(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showGameEndDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(if (userHasWon) "You won 🎉" else "You lost 😅") },
            icon = {
                if (userHasWon) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chess_queen),
                        contentDescription = "Crown",
                        tint = MaterialTheme.extendedColors.win,
                        modifier = Modifier.size(60.dp)
                    )
                }
            },
            text = { EndDialogText(gameSettings.difficulty, seconds, userHasWon) },
            confirmButton = {
                Button(
                    onClick = {
                        showGameEndDialog = false
                        onBack()
                    }
                ) {
                    Text("Continue")
                }
            }
        )
    }

    EndDialogText(gameSettings.difficulty, seconds, userHasWon)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SudokuTopAppBar(
                scrollBehavior = scrollBehavior,
                isPaused = isPaused,
                togglePaused = ::togglePaused,
                onBack = { setExitDialogVisibility(true) },
                difficulty = gameSettings.difficulty,
                seconds = seconds
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
                hints = hints,
                mistakes = mistakes,
                maxHints = gameSettings.hints,
                maxMistakes = gameSettings.mistakes,
            )
            SudokuBoard(
                boardData = sudokuBoard,
                selectedTileIndices = selectedTileIndices,
                selectTile = ::selectTile,
                isPaused = isPaused,
                togglePaused = ::togglePaused
            )
            NumberButtons(onNumberClick = ::setTileValue, isPaused = isPaused)
            ActionButtons(
                isWritingNotes = isWritingNotes,
                toggleEditing = { isWritingNotes = !isWritingNotes },
                eraseTile = ::eraseSelectedTile,
                undoLastAction = ::undoLastAction,
                canUndo = undoableActions.isNotEmpty(),
                generateHint = ::generateHint,
                canGenerateHint = hints < gameSettings.hints
            )
        }
    }
}