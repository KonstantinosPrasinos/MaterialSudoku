package com.example.multiplayersudoku.classes

import padWithZeros
import generateRandomNumber

class GameRepository(
    private val database: FirebaseDatabase,
    private val currentUserId: String,
    private val gameRef: DatabaseReference,
    public var canonicalBoard: SudokuBoardData,
    public var difficulty: Difficulty,
) {
    var gameId: String = "";

    companion object {
        fun initializeNewGame(difficulty: Difficulty): GameRepository {
            val database = FirebaseDatabase.getInstance();
            val currentUserId: String = getCurrentUserId();
            val canonicalBoard: SudokuBoardData = SudokuBoardData.generateRandom(difficulty);

            val playerSolutions: MutableMap<String, List<List<Int>>> = mutableMapOf()

            if (currentUserId === null) {
                // unauthorized don't allow online play
            }

            playerSolutions[currentUserId] = canonicalBoard.getAsNumberList()

            val gameId = padWithZeros(generateRandomNumber(0, 999_999));
        
            val gamesRef = database.getReference("games");
            val gameRef = gamesRef.child(gameId)

            return GameRepository(database, currentUserId, gameRef, canonicalBoard, difficulty)
        }
    }

    fun observeGame(gameId: String): Flow<SudokuBoardDocument?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Deserialize data and send it through the Flow
                val game = snapshot.getValue(SudokuBoardDocument::class.java)
                trySend(game)
            }

            override fun onCancelled(error: DatabaseError) {
                // Close the channel if the database call is cancelled
                close(error.toException())
            }
        }

        // Start listening
        gameRef.addValueEventListener(listener)

        // Crucial: Suspend until the Flow is closed (i.e., ViewModel is cleared), then remove listener
        awaitClose {
            gameRef.removeEventListener(listener)
            println("Removed listener for game $gameId")
        }
    }
}