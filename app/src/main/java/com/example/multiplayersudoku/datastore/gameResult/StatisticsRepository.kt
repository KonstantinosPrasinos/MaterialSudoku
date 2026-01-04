package com.example.multiplayersudoku.datastore.gameResult

import android.content.ContentValues.TAG
import android.util.Log
import com.example.multiplayersudoku.views.statisticsView.StatisticsSummary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class StatisticsRepository(private val dao: GameResultDao) {
    suspend fun saveResult(result: GameResult) {
        dao.insertResult(result)
    }

    suspend fun saveResultToFirestore(result: GameResult) {
        val authUser = Firebase.auth.currentUser ?: return
        val db = Firebase.firestore

        result.userRef = db.collection("users").document(authUser.uid)

        try {
            db.collection("game_rounds").document(result.id).set(result).await()
            Log.d(TAG, "Result added to remote database")
        } catch (e: Exception) {
            Log.d(TAG, "Error adding result to remote database", e)
        }
    }

    suspend fun syncResultsToAndFromFirestore() {
        val authUser = Firebase.auth.currentUser ?: return
        val db = Firebase.firestore

        val firebaseResults =
            db.collection("game_rounds").whereEqualTo("userRef", db.collection("users").document(authUser.uid)).get()
                .await()
        val localResults = this.getAllResults().first()

        val localGamesNotInFirebase =
            localResults.filter { localResult -> !firebaseResults.any { it.id == localResult.id } }
        val firebaseGamesNotInLocal =
            firebaseResults.filter { firebaseResult -> !localResults.any { it.id == firebaseResult.id } }

        for (result in localGamesNotInFirebase) {
            db.collection("game_rounds").document(result.id).set(result).await()
        }

        for (result in firebaseGamesNotInLocal) {
            val gameResult = GameResult(
                id = result.id,
                difficulty = result.getString("difficulty") ?: "EASY",
                durationSeconds = result.getLong("durationSeconds") ?: 0,
                wasCompleted = result.getBoolean("wasCompleted") ?: true,
                mistakesCount = result.getLong("mistakesCount")?.toInt() ?: 0,
                hintsUsed = result.getLong("hintsUsed")?.toInt() ?: 0,
                timestamp = result.getLong("timestamp") ?: 0,
                isMultiplayer = result.getBoolean("multiplayer") ?: false,
                opponentName = result.getString("opponentName"),
                wonAgainstOpponent = result.getBoolean("wonAgainstOpponent"),
                userRef = result.getDocumentReference("userRef"),
                opponentRef = result.getDocumentReference("opponentRef"),
            )
            dao.insertResult(gameResult)
        }

        if (localGamesNotInFirebase.isNotEmpty()) {
            db.runBatch { batch ->
                for (result in localGamesNotInFirebase) {
                    val resultDoc = db.collection("game_rounds").document(result.id)
                    batch.set(resultDoc, result)
                }
            }.await()
        }

        Log.d(
            TAG,
            "Finished syncing data to and from remote database: ${localGamesNotInFirebase.size} local, ${firebaseGamesNotInLocal.size} remote"
        )
    }

    fun getAverageDurationByDifficulty(difficulty: String): Flow<Long?> = dao.getAverageDurationByDifficulty(difficulty)
    fun getBestTimeByDifficulty(difficulty: String): Flow<Long?> = dao.getBestTimeByDifficulty(difficulty)
    fun getTotalGamesByDifficulty(difficulty: String): Flow<Int> = dao.getTotalGamesByDifficulty(difficulty)
    fun getTotalDurationByDifficulty(difficulty: String): Flow<Long?> = dao.getTotalDurationByDifficulty(difficulty)
    fun getCompletedGamesByDifficulty(difficulty: String): Flow<Int> = dao.getCompletedGamesByDifficulty(difficulty)

    fun getAverageDuration(): Flow<Long?> = dao.getAverageDuration()
    fun getBestTime(): Flow<Long?> = dao.getBestTime()
    fun getTotalGames(): Flow<Int> = dao.getTotalGames()
    fun getTotalDuration(): Flow<Long?> = dao.getTotalDuration()
    fun getCompletedGames(): Flow<Int> = dao.getCompletedGames()
    fun getAllResults(): Flow<List<GameResult>> = dao.getAllResults()

    fun getStatisticsSummary(difficulty: String?): Flow<StatisticsSummary> = dao.getStatisticsSummary(difficulty)
}