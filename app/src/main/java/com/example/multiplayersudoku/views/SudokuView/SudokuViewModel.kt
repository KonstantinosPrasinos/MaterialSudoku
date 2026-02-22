package com.example.multiplayersudoku.views.SudokuView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.classes.RoomData
import com.example.multiplayersudoku.classes.SudokuBoardData
import com.example.multiplayersudoku.classes.SudokuTileData
import com.example.multiplayersudoku.datastore.gameResult.GameResult
import com.example.multiplayersudoku.datastore.gameResult.StatisticsRepository
import com.example.multiplayersudoku.utils.attemptSolve
import com.example.multiplayersudoku.utils.updateNotes
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val repository: SudokuViewRepository
) : ViewModel() {
    private lateinit var gameSettings: GameSettings

    var selectedTileIndices by mutableStateOf<List<Int?>>(listOf(null, null))
        private set

    var sudokuBoard by mutableStateOf(SudokuBoardData.generateEmpty())
        private set

    var isPaused by mutableStateOf(false)
        private set

    var isWritingNotes by mutableStateOf(false)
        private set

    var mistakes by mutableStateOf(0)
        private set


    var hints by mutableStateOf(0)
        private set

    var undoableActions by mutableStateOf<List<SudokuTileData>>(emptyList())
        private set

    var showGameEndDialog by mutableStateOf(false)
        private set

    var userHasWon by mutableStateOf(false)
        private set

    var doTimer by mutableStateOf(true)
        private set

    var seconds by mutableStateOf(0)
        private set

    var showExitDialog by mutableStateOf(false)
        private set

    var isFirstRun by mutableStateOf(true)
        private set

    var roomData: RoomData? by mutableStateOf(null)
        private set

    var userId: String? by mutableStateOf(null)
        private set

    var user: FirebaseUser? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(true)
        private set

    private var timerJob: Job? = null

    private fun initializeMultiplayerAsOwner() {
        val newSudokuBoard = SudokuBoardData.generateRandom(gameSettings.difficulty)
        val canonBoard =
            newSudokuBoard.board.map { it.map { tile -> if (tile.value != null) tile.value else -1 } as List<Int> }
        val updatedRoomData = roomData?.copy(
            ownerBoard = newSudokuBoard.board,
            opponentBoard = newSudokuBoard.board,
            canonBoard = canonBoard
        )

        // Update the local state on the Main thread
        viewModelScope.launch(Dispatchers.Main) {
            sudokuBoard = newSudokuBoard
            roomData = updatedRoomData
        }


        if (updatedRoomData != null) {
            repository.setAllBoards(updatedRoomData) // This should be a suspend fun
        }
    }

    private fun initializeMultiplayerAsOpponent() {

    }

    fun init(settings: GameSettings, roomCode: String?) {
        gameSettings = settings
        user = Firebase.auth.currentUser
        // Don't set isLoading here, it's true by default

        viewModelScope.launch { // The parent coroutine
            try {
                if (!roomCode.isNullOrEmpty()) {
                    // --- Multiplayer Logic ---
                    userId = user?.uid
                    if (userId == null) return@launch // Early exit

                    // Perform network and CPU work in the background
                    withContext(Dispatchers.IO) { // Use IO for network, Default for CPU
                        val fetchedRoomData = repository.getRoomData(roomCode, userId!!)
                        withContext(Dispatchers.Main) {
                            roomData = fetchedRoomData
                        }

                        if (fetchedRoomData.ownerPath == userId) {
                            // This contains the CPU-heavy board generation
                            initializeMultiplayerAsOwner()
                        } else {
                            initializeMultiplayerAsOpponent()
                        }
                    }
                } else {
                    // --- Solo Player Logic ---
                    // Generate the board on the Default dispatcher (for CPU-bound work)
                    val newBoard = withContext(Dispatchers.Default) {
                        SudokuBoardData.generateRandom(gameSettings.difficulty)
                    }
                    // Update the UI state on the Main thread
                    withContext(Dispatchers.Main) {
                        sudokuBoard = newBoard
                    }
                }
            } finally {
                // This block runs whether the try block succeeds or fails
                withContext(Dispatchers.Main) {
                    isLoading = false
                    startTimer()
                }
            }
        }
    }

    private fun checkForMistakes(number: Int, row: Int, col: Int): Boolean {
        val newBoard = sudokuBoard.board.toMutableList().map { it.toMutableList() }
        val tileToUpdate = newBoard[row][col]

        val isDuplicate =
            com.example.multiplayersudoku.utils.checkRow(newBoard, tileToUpdate, number) ||
                    com.example.multiplayersudoku.utils.checkCol(newBoard, tileToUpdate, number) ||
                    com.example.multiplayersudoku.utils.checkGrid(newBoard, tileToUpdate, number)

        if (isDuplicate) return true

        // Place the value in the field and test
        newBoard[row][col] = tileToUpdate.copy(value = number)

        val solvedBoard = attemptSolve(newBoard)

        return !solvedBoard.isSolved
    }

    private fun checkForWin(board: List<List<SudokuTileData>>): Boolean {
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

    private fun checkGrid(board: List<List<SudokuTileData>>, tile: SudokuTileData): Boolean {
        val startRow = tile.rowIndex!! - (tile.rowIndex!! % 3)
        val startCol = tile.colIndex!! - (tile.colIndex!! % 3)
        var emptyTileFound = false

        for (i in startRow until startRow + 3) {
            for (j in startCol until startCol + 3) {
                val gridTile = board[i][j]
                if (gridTile.value == null || gridTile.isMistake) {
                    emptyTileFound = true
                    break
                }
            }
            if (emptyTileFound) {
                break
            }
        }

        return !emptyTileFound
    }

    private fun checkRow(board: List<List<SudokuTileData>>, tile: SudokuTileData): Boolean {
        val row = board[tile.rowIndex!!]

        var emptyTileFound = false

        for (tile in row) {
            if (tile.value == null || tile.isMistake) {
                emptyTileFound = true
                break
            }
        }

        return !emptyTileFound
    }

    private fun checkCol(board: List<List<SudokuTileData>>, tile: SudokuTileData): Boolean {
        val col = board.map { it[tile.colIndex!!] }

        var emptyTileFound = false

        for (tile in col) {
            if (tile.value == null || tile.isMistake) {
                emptyTileFound = true
                break
            }
        }

        return !emptyTileFound
    }

    private fun addToUndoableActions(action: SudokuTileData) {
        undoableActions += action
    }

    private fun runAnimations(
        tileToUpdate: SudokuTileData,
        rowCompleted: Boolean,
        colCompleted: Boolean,
        gridCompleted: Boolean
    ) {
        val rowIndex = tileToUpdate.rowIndex!!
        val colIndex = tileToUpdate.colIndex!!

        val startRow = rowIndex - (rowIndex % 3)
        val startCol = colIndex - (colIndex % 3)

        val tilesToAnimate = mutableListOf<Pair<Int, Int>>()

        if (gridCompleted) {
            for (i in startRow until startRow + 3) {
                for (j in startCol until startCol + 3) {
                    tilesToAnimate.add(Pair(i, j))
                }
            }
        }

        val animatedTiles = mutableListOf<Pair<Int, Int>>()

        viewModelScope.launch {
            for (i in 0 until 9) {
                val newBoard = sudokuBoard.board.map { it.toMutableList() }

                if (rowCompleted) {
                    val tileToAnimate = newBoard[rowIndex][i]
                    newBoard[rowIndex][i] = tileToAnimate.copy(isCompleted = true)
                    animatedTiles.add(Pair(rowIndex, i))
                }
                if (colCompleted) {
                    val tileToAnimate = newBoard[i][colIndex]
                    newBoard[i][colIndex] = tileToAnimate.copy(isCompleted = true)
                    animatedTiles.add(Pair(i, colIndex))
                }
                if (gridCompleted) {
                    val tileToAnimate = newBoard[tilesToAnimate[i].first][tilesToAnimate[i].second]
                    newBoard[tilesToAnimate[i].first][tilesToAnimate[i].second] =
                        tileToAnimate.copy(isCompleted = true)
                    animatedTiles.add(tilesToAnimate[i])
                }

                // Assign the new board state to trigger recomposition
                sudokuBoard = sudokuBoard.copy(board = newBoard)
                delay(50) // A shorter delay often looks better for animations
            }

            // Reset the animated tiles so that they can be animated again
            val remainingTime: Long = 9 * 500 - 9 * 50
            delay(remainingTime)
            val newBoard = sudokuBoard.board.map { it.toMutableList() }

            for (tile in animatedTiles) {
                val tileToAnimate = newBoard[tile.first][tile.second]
                newBoard[tile.first][tile.second] = tileToAnimate.copy(isCompleted = false)
            }

            sudokuBoard = sudokuBoard.copy(board = newBoard)
        }
    }

    fun undoLastAction() {
        val solvedBoard = attemptSolve(sudokuBoard.board)

        sudokuBoard = sudokuBoard.copy(
            board = solvedBoard.boardData.map { it }
        )

        if (undoableActions.isEmpty()) return

        val lastAction = undoableActions.last()

        undoTileState(lastAction)
        undoableActions = undoableActions.dropLast(1)
    }

    private fun undoTileState(prevTileState: SudokuTileData) {
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

    private fun toggleGameEndDialog() {
        showGameEndDialog = !showGameEndDialog
    }

    private fun onGameFinished(result: GameResult) {
        // Add the result to local state and firebase
        viewModelScope.launch {
            statisticsRepository.saveResult(result)
            statisticsRepository.saveResultToFirestore(result)
        }
    }

    private fun finalizeGame() {
        doTimer = false

        val result = GameResult(
            difficulty = gameSettings.difficulty.name,
            durationSeconds = seconds.toLong(),
            wasCompleted = userHasWon,
            mistakesCount = mistakes,
            hintsUsed = hints
        )

        this.onGameFinished(result)

        toggleGameEndDialog()
    }

    fun setExitDialogVisibility(value: Boolean = true) {
        showExitDialog = value
        updatePauseState(value)
    }

    private fun calculateBoardFillPercentage(boardData: List<List<SudokuTileData>>): Float {
        var filledCells = 0
        var totalCells = 0f

        for (row in boardData) {
            for (tile in row) {
                if (tile.value != null && !tile.isMistake && tile.isEditable) {
                    filledCells++;
                }
                if (tile.isEditable) {
                    totalCells++
                }
            }
        }

        return filledCells / totalCells
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

        var userHasWon = false

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

            if (roomData != null) {
                if (roomData?.ownerPath == userId) {
                    roomData = roomData?.copy(
                        ownerBoardPercentage = calculateBoardFillPercentage(newBoard)
                    )
                } else {
                    roomData = roomData?.copy(
                        opponentBoardPercentage = calculateBoardFillPercentage(newBoard)
                    )
                }
//                viewModelScope.launch {
//                    repository.setBoardPercentages(
//                        roomData!!.roomCode,
//                        roomData!!.ownerBoardPercentage,
//                        roomData!!.opponentBoardPercentage
//                    )
//                }
            }

            if (checkForWin(newBoard)) {
                userHasWon = true
                finalizeGame()
            }
        }

        sudokuBoard = sudokuBoard.copy(
            board = newBoard.map { it }
        )

        // do animations here
        val rowCompleted = checkRow(newBoard, tileToUpdate)
        val colCompleted = checkCol(newBoard, tileToUpdate)
        val gridCompleted = checkGrid(newBoard, tileToUpdate)

        if (rowCompleted || colCompleted || gridCompleted) {
            runAnimations(tileToUpdate, rowCompleted, colCompleted, gridCompleted)
        }
    }

    fun selectTile(row: Int?, col: Int?) {
        if (isPaused) return

        if (row == selectedTileIndices[0] && col == selectedTileIndices[1]) {
            selectedTileIndices = listOf(null, null)
        } else {
            selectedTileIndices = listOf(row, col)
        }
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
            val isMistake = checkForMistakes(i, row, col)
            if (isMistake) {
                continue
            }

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

    fun toggleWritingNotes() {
        isWritingNotes = !isWritingNotes
    }

    fun updateShowGameEndDialogState(value: Boolean) {
        showGameEndDialog = value
    }

    fun updateShowExitDialogState(value: Boolean) {
        showExitDialog = value
    }

    fun updateFirstRunState(value: Boolean) {
        isFirstRun = value
    }

    // Seconds timer
    fun updatePauseState(isPaused: Boolean) {
        this.isPaused = isPaused
        if (isPaused) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    fun togglePaused() {
        isPaused = !isPaused
        if (isPaused) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        // Ensure doTimer is true and the job is not already running
        if (doTimer && timerJob == null) {
            timerJob = viewModelScope.launch {
                while (!isPaused && doTimer) { // The condition from your LaunchedEffect
                    delay(1000)
                    incrementSeconds() // Use the public setter method
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun incrementSeconds() {
        seconds++
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}