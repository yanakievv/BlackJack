package com.example.blackjack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class], version = 6, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {
    abstract val userDAO: UserDAO

    companion object{
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_statistics_db"
                    )
                        //.fallbackToDestructiveMigration()
                        .addMigrations(MIGRATION_4_5)
                        .addMigrations(MIGRATION_5_6)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) { //added a wallet for future implementation of bets
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                CREATE TABLE new_user_data (
                    userId INTEGER PRIMARY KEY NOT NULL,
                    fb_ID TEXT NOT NULL DEFAULT '',
                    username TEXT NOT NULL DEFAULT '',
                    wins INTEGER NOT NULL DEFAULT 0,
                    losses INTEGER NOT NULL DEFAULT 0,
                    split_hands_won INTEGER NOT NULL DEFAULT 0,
                    split_hands_lost INTEGER NOT NULL DEFAULT 0,
                    doubles_won INTEGER NOT NULL DEFAULT 0,
                    current_streak INTEGER NOT NULL DEFAULT 0,
                    best_streak INTEGER NOT NULL DEFAULT 0,
                    wallet INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent())
                database.execSQL("""
                INSERT INTO new_user_data (userId, fb_ID, username, wins, losses, split_hands_won, split_hands_lost, doubles_won, current_streak, best_streak)
                SELECT userId, fb_ID, username, wins, losses, split_hands_won, split_hands_lost, doubles_won, current_streak, best_streak FROM user_data
                """.trimIndent())
                database.execSQL("DROP TABLE user_data")
                database.execSQL("ALTER TABLE new_user_data RENAME TO user_data")
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) { //renamed fb_ID to acc_ID since its now possible to access statistics with a Google account
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                CREATE TABLE new_user_data (
                    userId INTEGER PRIMARY KEY NOT NULL,
                    acc_ID TEXT NOT NULL DEFAULT '',
                    username TEXT NOT NULL DEFAULT '',
                    wins INTEGER NOT NULL DEFAULT 0,
                    losses INTEGER NOT NULL DEFAULT 0,
                    split_hands_won INTEGER NOT NULL DEFAULT 0,
                    split_hands_lost INTEGER NOT NULL DEFAULT 0,
                    doubles_won INTEGER NOT NULL DEFAULT 0,
                    current_streak INTEGER NOT NULL DEFAULT 0,
                    best_streak INTEGER NOT NULL DEFAULT 0,
                    wallet INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent())
                database.execSQL("""
                INSERT INTO new_user_data (userId, acc_ID, username, wins, losses, split_hands_won, split_hands_lost, doubles_won, current_streak, best_streak, wallet)
                SELECT userId, fb_ID, username, wins, losses, split_hands_won, split_hands_lost, doubles_won, current_streak, best_streak, wallet FROM user_data
                """.trimIndent())
                database.execSQL("DROP TABLE user_data")
                database.execSQL("ALTER TABLE new_user_data RENAME TO user_data")
            }
        }
    }
}