package com.example.multiplayersudoku.views.SudokuView

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.classes.SudokuBoardData
import com.example.multiplayersudoku.classes.SudokuTileData
import com.example.multiplayersudoku.utils.attemptSolve
import com.example.multiplayersudoku.utils.checkCol
import com.example.multiplayersudoku.utils.checkGrid
import com.example.multiplayersudoku.utils.checkRow
import com.example.multiplayersudoku.utils.updateNotes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SudokuView(onBack: () -> Unit, gameSettings: GameSettings) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showExitDialog by remember { mutableStateOf(false) }
    var showGameEndDialog by remember { mutableStateOf(false) }

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
    fun addToUndoableActions(action: SudokuTileData) = undoableActions.add(action)

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
                mistakes = mistakes + 1
                if (mistakes >= gameSettings.mistakes) {
                    // Game over
                    // TODO: add sad face 😅 and you lost dialog
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

    BackHandler(enabled = !showExitDialog) {
        isPaused = true
        showExitDialog = true
    }

    fun toggleExitDialog() {
        showExitDialog = !showExitDialog
        togglePaused()
    }

    fun toggleGameEndDialog() {
        showGameEndDialog = !showGameEndDialog
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { toggleExitDialog() },
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
                TextButton(onClick = { toggleExitDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SudokuTopAppBar(
                scrollBehavior = scrollBehavior,
                isPaused = isPaused,
                togglePaused = ::togglePaused,
                onBack = ::toggleExitDialog,
                difficulty = gameSettings.difficulty,
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