package com.example.multiplayersudoku.utils

import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.classes.SudokuTileData

class ChangesMade(var nChanges: Int, var boardData: List<List<SudokuTileData>>)
class SolvedBoard(var isSolved: Boolean, var boardData: List<List<SudokuTileData>>)

fun checkRow(
    boardData: List<List<SudokuTileData>>,
    tileToCheck: SudokuTileData,
    value: Int,
    checkNotes: Boolean = false
): Boolean {
    for (tile in boardData[tileToCheck.rowIndex!!]) {
        // Skip the tile itself
        if (tileToCheck == tile) continue

        // Skip tiles that are known mistakes
        if (tile.isMistake) continue

        if (checkNotes && tile.notes.contains(value)) return true
        if (tile.value == value) return true
    }
    return false
}

fun checkCol(
    boardData: List<List<SudokuTileData>>,
    tileToCheck: SudokuTileData,
    value: Int,
    checkNotes: Boolean = false
): Boolean {
    for (row in boardData) {
        val testingTile = row[tileToCheck.colIndex!!]

        // Skip the tile itself
        if (testingTile == tileToCheck) continue

        // Skip tiles that are known mistakes
        if (testingTile.isMistake) continue

        if (checkNotes && testingTile.notes.contains(value)) return true
        if (testingTile.value == value) return true
    }
    return false
}

fun checkGrid(
    boardData: List<List<SudokuTileData>>,
    tileToCheck: SudokuTileData,
    value: Int,
    checkNotes: Boolean = false
): Boolean {
    val startRow = tileToCheck.rowIndex!! / 3 * 3
    val startCol = tileToCheck.colIndex!! / 3 * 3
    for (r in startRow until startRow + 3) {
        for (c in startCol until startCol + 3) {
            val testingTile = boardData[r][c]

            // Skip the tile itself
            if (testingTile == tileToCheck) continue

            // Skip tiles that are known mistakes
            if (testingTile.isMistake) continue

            if (checkNotes && testingTile.notes.contains(value)) return true
            if (testingTile.value == value) return true
        }
    }
    return false
}


fun updateNotes(
    boardData: List<List<SudokuTileData>>,
    tile: SudokuTileData,
    value: Int
): List<List<SudokuTileData>> {
    // Remove the note from the row
    for (tile in boardData[tile.rowIndex!!]) {
        if (tile.notes.contains(value)) {
            tile.notes.remove(value)
        }
    }

    // Remove the note from the column
    for (row in boardData) {
        if (row[tile.colIndex!!].notes.contains(value)) {
            row[tile.colIndex!!].notes.remove(value)
        }
    }

    // Remove the note from the 3x3 grid
    val startRow = tile.rowIndex!! / 3 * 3
    val startCol = tile.colIndex!! / 3 * 3

    for (r in startRow until startRow + 3) {
        for (c in startCol until startCol + 3) {
            if (boardData[r][c].notes.contains(value)) {
                boardData[r][c].notes.remove(value)
            }
        }
    }

    return boardData
}


// Sudoku Techniques
// Level 1: Easy techniques

// Tile has only one possible solution
fun nakedSingle(boardData: List<List<SudokuTileData>>): ChangesMade {
    var newBoard = boardData.map { it.toMutableList() }.toMutableList()
    var changesMade = 0

    for (row in newBoard) {
        for (tile in row) {
            if (tile.value == null && tile.notes.size == 1) {
                tile.value = tile.notes[0]
                newBoard = updateNotes(
                    newBoard,
                    tile,
                    tile.notes[0]
                ) as MutableList<MutableList<SudokuTileData>>
                changesMade++;
            }
        }
    }

    return ChangesMade(nChanges = changesMade, boardData = newBoard)
}

// This checks all the notes in the row, column and grid for tiles that can be solved only one way
fun hiddenSingle(boardData: List<List<SudokuTileData>>): ChangesMade {
    var newBoard = boardData.map { it.toMutableList() }.toMutableList()
    var changesMade = 0

    for (row in newBoard) {
        for (tile in row) {
            if (tile.value != null) continue
            for (note in tile.notes) {
                val foundInRow = checkRow(newBoard, tile, note, true)
                val foundInCol = checkCol(newBoard, tile, note, true)
                val foundInGrid = checkGrid(newBoard, tile, note, true)

                if (foundInRow || foundInCol || foundInGrid) continue

                if (tile.notes.size == 1) {
                    tile.value = tile.notes[0]
                    newBoard = updateNotes(
                        newBoard,
                        tile,
                        tile.notes[0]
                    ) as MutableList<MutableList<SudokuTileData>>
                    changesMade++
                }
            }
        }
    }

    return ChangesMade(nChanges = changesMade, boardData = newBoard)
}

// This checks each tile if it is the only possible solution for a number within the row, column and grid
fun scanning(boardData: List<List<SudokuTileData>>): ChangesMade {
    var newBoard = boardData.map { it.toMutableList() }.toMutableList()
    var changesMade = 0;

    for (row in newBoard) {
        for (tile in row) {
            if (tile.value != null) continue
            for (i in 1..9) {
                var valueFound = checkRow(newBoard, tile, i, true)
                if (valueFound) continue

                valueFound = checkCol(newBoard, tile, i, true)
                if (valueFound) continue

                valueFound = checkGrid(newBoard, tile, i, true)
                if (valueFound) continue

                tile.value = i
                newBoard =
                    updateNotes(newBoard, tile, i) as MutableList<MutableList<SudokuTileData>>
                changesMade++;
            }
        }
    }

    return ChangesMade(nChanges = changesMade, boardData = newBoard)
}


// Helper function that marks all tile's notes
fun markNotes(boardData: List<List<SudokuTileData>>): List<List<SudokuTileData>> {
    val newBoard = boardData.map { it.toMutableList() }.toMutableList()

    for (row in newBoard) {
        for (tile in row) {
            if (tile.value != null) continue
            val newNotes = tile.notes.toMutableList()

            for (i in 1..9) {
                if (tile.notes.contains(i)) continue

                // Check the row
                var valueFound = checkRow(newBoard, tile, i)
                if (valueFound) continue

                // Check the column
                valueFound = checkCol(newBoard, tile, i)
                if (valueFound) continue

                // Check the 3x3 grid
                valueFound = checkGrid(newBoard, tile, i)
                if (valueFound) continue

                // If not found anywhere, add the note
                newNotes.add(i)
            }

            if (newNotes.size > tile.notes.size) {
                newBoard[tile.rowIndex!!][tile.colIndex!!] = tile.copy(notes = newNotes)
            }
        }
    }

    return newBoard
}

fun attemptSolve(
    boardData: List<List<SudokuTileData>>,
    difficulty: Difficulty = Difficulty.EASY
): SolvedBoard {
    var newBoard = markNotes(boardData)
    var totalChangesMade: Int

    if (difficulty == Difficulty.EASY) {
        // Try to solve it with the techniques
        do {
            totalChangesMade = 0

            val nakedChangesMade = nakedSingle(newBoard)
            totalChangesMade += nakedChangesMade.nChanges
            newBoard = nakedChangesMade.boardData


            val hiddenChangesMade = hiddenSingle(newBoard)
            totalChangesMade += hiddenChangesMade.nChanges
            newBoard = hiddenChangesMade.boardData

            val scanningChangesMade = scanning(newBoard)
            totalChangesMade += scanningChangesMade.nChanges
            newBoard = scanningChangesMade.boardData
        } while (totalChangesMade > 0)

        print("Can be solved: ${checkBoardFilled(newBoard)}")

        return SolvedBoard(checkBoardFilled(newBoard), newBoard)
    }

    return SolvedBoard(false, newBoard)
}