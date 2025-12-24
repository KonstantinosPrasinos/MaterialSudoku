package com.example.multiplayersudoku.datastore

import android.content.Context
import com.example.multiplayersudoku.datastore.gameResult.GameResultDao
import com.example.multiplayersudoku.datastore.gameResult.StatisticsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideGameResultDao(database: AppDatabase): GameResultDao {
        return database.gameResultDao()
    }

    @Provides
    fun provideStatisticsRepository(dao: GameResultDao): StatisticsRepository {
        return StatisticsRepository(dao)
    }
}