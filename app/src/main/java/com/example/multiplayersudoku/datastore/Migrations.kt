package com.example.multiplayersudoku.datastore

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Write the SQL to update the table
        database.execSQL("ALTER TABLE game_results ADD COLUMN userRef TEXT DEFAULT NULL")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Write the SQL to update the table
        database.execSQL("ALTER TABLE game_results ADD COLUMN opponentRef TEXT DEFAULT NULL")
    }
}