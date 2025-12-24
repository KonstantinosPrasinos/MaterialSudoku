package com.example.multiplayersudoku.datastore.gameResult

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.multiplayersudoku.views.statisticsView.StatisticsSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {
    @Insert
    suspend fun insertResult(result: GameResult)

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<GameResult>>

    @Query("SELECT AVG(durationSeconds) FROM game_results WHERE wasCompleted = 1")
    fun getAverageDuration(): Flow<Long?>

    @Query("SELECT MIN(durationSeconds) FROM game_results WHERE wasCompleted = 1")
    fun getBestTime(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM game_results")
    fun getTotalGames(): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM game_results")
    fun getTotalDuration(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM game_results WHERE wasCompleted = 1")
    fun getCompletedGames(): Flow<Int>


    // Difficulty based statistics
    @Query("SELECT AVG(durationSeconds) FROM game_results WHERE difficulty = :difficulty AND wasCompleted = 1")
    fun getAverageDurationByDifficulty(difficulty: String): Flow<Long?>

    @Query("SELECT MIN(durationSeconds) FROM game_results WHERE difficulty = :difficulty AND wasCompleted = 1")
    fun getBestTimeByDifficulty(difficulty: String): Flow<Long?>

    @Query("SELECT COUNT(*) FROM game_results WHERE difficulty = :difficulty")
    fun getTotalGamesByDifficulty(difficulty: String): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM game_results WHERE difficulty = :difficulty")
    fun getTotalDurationByDifficulty(difficulty: String): Flow<Long?>

    @Query("SELECT COUNT(*) FROM game_results WHERE difficulty = :difficulty AND wasCompleted = 1")
    fun getCompletedGamesByDifficulty(difficulty: String): Flow<Int>

    @Query(
        """
    SELECT 
        AVG(CASE WHEN wasCompleted = 1 THEN durationSeconds ELSE NULL END) as averageDuration,
        MIN(CASE WHEN wasCompleted = 1 THEN durationSeconds ELSE NULL END) as bestTime,
        COUNT(*) as totalGames,
        SUM(CASE WHEN wasCompleted = 1 THEN 1 ELSE 0 END) as completedGames,
        SUM(durationSeconds) as totalDuration
    FROM game_results 
    WHERE (:difficulty IS NULL OR difficulty = :difficulty)
"""
    )
    fun getStatisticsSummary(difficulty: String?): Flow<StatisticsSummary>

}