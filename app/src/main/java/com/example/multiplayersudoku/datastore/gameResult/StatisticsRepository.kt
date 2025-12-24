package com.example.multiplayersudoku.datastore.gameResult

import com.example.multiplayersudoku.views.statisticsView.StatisticsSummary
import kotlinx.coroutines.flow.Flow

class StatisticsRepository(private val dao: GameResultDao) {
    suspend fun saveResult(result: GameResult) {
        dao.insertResult(result)
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