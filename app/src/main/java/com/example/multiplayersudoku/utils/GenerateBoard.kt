package com.example.multiplayersudoku.utils

import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.classes.SudokuTileData
import kotlinx.coroutines.yield

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

fun countSolutions(boardData: List<List<SudokuTileData>>, row: Int = 0, col: Int = 0, maxSolutions: Int = 2): Int {
    // Max col is 8 so 9 should be on the next row
    if (col == 9) {
        return countSolutions(boardData, row + 1, 0, maxSolutions)
    }

    // Started to check over max rows so the puzzle is solved
    if (row == 9) {
        return 1
    }

    val currentTile = boardData[row][col]

    if (currentTile.value != null) {
        return countSolutions(boardData, row, col + 1, maxSolutions)
    }

    var totalSolutions = 0

    for (num in 1..9) {
        val isValid = !checkRow(boardData, currentTile, num) &&
                !checkCol(boardData, currentTile, num) &&
                !checkGrid(boardData, currentTile, num)

        if (isValid) {
            currentTile.value = num

            totalSolutions += countSolutions(boardData, row, col + 1, maxSolutions)

            currentTile.value = null

            if (totalSolutions >= maxSolutions) {
                return totalSolutions
            }
        }
    }

    return totalSolutions
}

fun hasUniqueSolution(boardData: List<List<SudokuTileData>>): Boolean {
    return countSolutions(boardData, maxSolutions = 2) == 1
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

suspend fun generateBoard(difficulty: Difficulty = Difficulty.EASY): com.example.multiplayersudoku.classes.SudokuBoardData {
    // Generate a filled board and capture the ground-truth solution matrix
    val filledBoard = generateFilledBoard()
    val solutionMatrix = filledBoard.map { row ->
        row.map { tile -> tile.value!! }
    }

    yield()

    // Create a copy for clue removal
    val puzzleBoard = filledBoard.map { row ->
        row.map { tile -> tile.copy() }
    }

    // Decide how many tiles to remove
    val positionsToRemove = when (difficulty) {
        Difficulty.EASY -> (36..45).random()
        Difficulty.MEDIUM -> (46..49).random()
        Difficulty.HARD -> (50..59).random()
    }

    // Remove the required positions
    val shuffledPositions = (0 until 81).shuffled()
    var emptyCount = 0

    for (pos in shuffledPositions) {
        if (emptyCount >= positionsToRemove) break

        val row = pos / 9
        val col = pos % 9

        // Remove the value temporarily and check if the board still has one unique solution
        val tempValue = puzzleBoard[row][col].value
        puzzleBoard[row][col].value = null

        // For EASY puzzles, verify that the board remains solvable using basic human techniques
        val isHumanSolvable = attemptSolve(puzzleBoard, difficulty).isSolved

        if (hasUniqueSolution(puzzleBoard) && isHumanSolvable) {
            puzzleBoard[row][col].isEditable = true
            emptyCount++
        } else {
            puzzleBoard[row][col].value = tempValue
        }
    }

    return com.example.multiplayersudoku.classes.SudokuBoardData(
        board = puzzleBoard,
        solution = solutionMatrix
    )
}