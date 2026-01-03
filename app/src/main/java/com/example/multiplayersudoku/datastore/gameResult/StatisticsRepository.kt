package com.example.multiplayersudoku.datastore.gameResult

import android.content.ContentValues.TAG
import android.util.Log
import com.example.multiplayersudoku.views.statisticsView.StatisticsSummary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
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

    fun getStatisticsSummary(difficulty: String?): Flow<StatisticsSummary> = dao.getStatisticsSummary(difficulty)
}