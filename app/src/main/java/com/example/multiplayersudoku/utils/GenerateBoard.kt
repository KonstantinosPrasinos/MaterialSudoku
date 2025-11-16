package com.example.multiplayersudoku.utils

import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.classes.SudokuTileData

fun checkBoardFilled(boardData: List<List<SudokuTileData>>): Boolean {
    for (row in boardData) {
        for (tile in row) {
            if (tile.value == null) return false
        }
    }

    return true
}

fun checkBoardValidity(boardData: List<List<SudokuTileData>>, row: Int, col: Int): Boolean {
    val foundInRow = checkRow(boardData, boardData[row][col], boardData[row][col].value!!)
    val foundInCol = checkCol(boardData, boardData[row][col], boardData[row][col].value!!)
    val foundInGrid = checkGrid(boardData, boardData[row][col], boardData[row][col].value!!)

    if (foundInGrid || foundInCol || foundInRow) return false
    return true
}

fun generateTile(boardData: List<List<SudokuTileData>>, row: Int, col: Int): Boolean {
    // 1. Base Case: If we've moved past the last column, go to the next row.
    if (col == 9) {
        return generateTile(boardData, row + 1, 0)
    }

    // 2. Success Case: If we've moved past the last row, the board is successfully filled.
    if (row == 9) {
        return true
    }

    // 3. Recursive Step: Try placing numbers in the current tile.
    val numbers =
        (1..9).shuffled() // .shuffled() already returns a new List, no need for toMutableList()

    for (number in numbers) {
        // Place a valid number
        boardData[row][col].value = number
        if (checkBoardValidity(boardData, row, col)) {
            // If the number is valid, try to solve the rest of the board from the next tile.
            // If the rest of the board is solvable, we have found a solution.
            if (generateTile(boardData, row, col + 1)) {
                return true // Success! Propagate 'true' up the call stack.
            }
        }
    }

    // 4. Backtracking: If the loop finishes, no number worked for this tile.
    // Reset the tile and return 'false' to signal the previous call to try a different number.
    boardData[row][col].value = null
    return false
}

fun generateFilledBoard(): List<List<SudokuTileData>> {
    val board = List(9) { row ->
        List(9) { column ->
            SudokuTileData(
                value = null,
                isEditable = false, // Or true, depending on your logic
                rowIndex = row,
                colIndex = column
            )
        }
    }

    // 2. Start the recursive generation process
    generateTile(board, 0, 0)

    return board
}

fun generateBoard(difficulty: Difficulty = Difficulty.EASY): List<List<SudokuTileData>> {
    // Generate a filled board
    val board = generateFilledBoard()
    var notSolved: Boolean
    var newBoard: List<List<SudokuTileData>>

    do {
        // Create a copy of the board
        newBoard = board.map { row ->
            row.map { tile ->
                tile.copy() // This creates a new SudokuTileData instance
            }
        }

        // Decide how many tiles to remove
        val positionsToRemove = when (difficulty) {
            Difficulty.EASY -> (20..27).random()
            Difficulty.MEDIUM -> (28..34).random()
            Difficulty.HARD -> (35..45).random()
        }

        // Remove the required positions
        val shuffledPositions = (0 until 81).shuffled()

        for (i in 0 until positionsToRemove) {
            val row = shuffledPositions[i] / 9
            val col = shuffledPositions[i] % 9

            newBoard[row][col].value = null
            newBoard[row][col].isEditable = true
        }


        // Attempt to solve the board
        val solvedBoard = attemptSolve(newBoard)

        notSolved = !solvedBoard.isSolved
    } while (notSolved)

    return newBoard
}