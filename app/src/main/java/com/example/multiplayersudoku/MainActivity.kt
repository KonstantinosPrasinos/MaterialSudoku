package com.example.multiplayersudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.SudokuBoardData
import com.example.multiplayersudoku.classes.SudokuTileData
import com.example.multiplayersudoku.components.ActionButtons
import com.example.multiplayersudoku.components.InfoBar
import com.example.multiplayersudoku.components.NumberButtons
import com.example.multiplayersudoku.components.SudokuBoard
import com.example.multiplayersudoku.components.SudokuTopAppBar
import com.example.multiplayersudoku.ui.theme.MultiplayerSudokuTheme
import com.example.multiplayersudoku.utils.AttemptSolve

val maxHints = 3;

class MainActivity : ComponentActivity() {
    val initialBoardValues = listOf(
        listOf(5, 3, null, null, 7, null, null, null, null),
        listOf(6, null, null, 1, 9, 5, null, null, null),
        listOf(null, 9, 8, null, null, null, null, 6, null),
        listOf(8, null, null, null, 6, null, null, null, 3),
        listOf(4, null, null, 8, null, 3, null, null, 1),
        listOf(7, null, null, null, 2, null, null, null, 6),
        listOf(null, 6, null, null, null, null, 2, 8, null),
        listOf(null, null, null, 4, 1, 9, null, null, 5),
        listOf(null, null, null, null, 8, null, null, 7, 9)
    )

    // Using the new secondary constructor
    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiplayerSudokuTheme {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

                var isPaused by remember { mutableStateOf(false) }
                var isWritingNotes by remember { mutableStateOf(false) }
                var sudokuBoard by remember {
                    mutableStateOf(
                        SudokuBoardData.fromInitialValues(
                            initialBoardValues
                        )
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
                    if (hints >= maxHints) return;
                    if (selectedTileIndices[0] == null || selectedTileIndices[1] == null) return

                    val row: Int = selectedTileIndices[0]!!
                    val col: Int = selectedTileIndices[1]!!

                    var tileToUpdate = sudokuBoard.board[row][col]

                    if (!tileToUpdate.isEditable) return
                    if (tileToUpdate.value != null && !tileToUpdate.isMistake) return

                    hints = hints + 1

                    val newBoard = sudokuBoard.board.toMutableList().map { it.toMutableList() }
                    tileToUpdate = newBoard[row][col]

                    for (i in 1..9) {
                        newBoard[row][col] = tileToUpdate.copy(value = i)

                        if (AttemptSolve(newBoard)) {
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

                    // Place the value in the field and test
                    newBoard[row][col] = tileToUpdate.copy(value = number)
                    return !AttemptSolve(newBoard)
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
                    val newBoard = sudokuBoard.board.toMutableList().map { it.toMutableList() }
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
                        // Check if the set value is a mistake
                        val isMistake = checkForMistakes(number, row, col)

                        if (isMistake) {
                            mistakes = mistakes + 1;
                        }

                        newBoard[row][col] =
                            tileToUpdate.copy(value = number, isMistake = isMistake)

                        // Add setting value to undoable actions
                        addToUndoableActions(tileToUpdate)
                    }

                    sudokuBoard = sudokuBoard.copy(
                        board = newBoard.map { it }
                    )
                }

                fun selectTile(row: Int?, col: Int?) {
                    if (isPaused) return;

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
                    if (undoableActions.isEmpty()) return

                    val lastAction = undoableActions.last()

                    undoTileState(lastAction);
                    undoableActions.removeAt(undoableActions.lastIndex)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        SudokuTopAppBar(
                            scrollBehavior = scrollBehavior,
                            isPaused = isPaused,
                            togglePaused = ::togglePaused
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InfoBar(hints = hints, mistakes = mistakes)
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
                            canGenerateHint = hints < maxHints
                        )
                    }
                }
            }
        }
    }
}