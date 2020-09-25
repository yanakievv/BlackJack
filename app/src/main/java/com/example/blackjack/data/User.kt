package com.example.blackjack.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    @ColumnInfo(name = "acc_ID", defaultValue = "")
    val accID: String,

    @ColumnInfo(name = "username", defaultValue = "")
    val username: String,

    @ColumnInfo(name = "wins", defaultValue = "0")
    val wins: Int = 0,

    @ColumnInfo(name = "losses", defaultValue = "0")
    val losses: Int = 0,

    @ColumnInfo(name = "split_hands_won", defaultValue = "0")
    val splitHandsWon: Int = 0,

    @ColumnInfo(name = "split_hands_lost", defaultValue = "0")
    val splitHandsLost: Int = 0,

    @ColumnInfo(name = "doubles_won", defaultValue = "0")
    val doublesWon: Int = 0,

    @ColumnInfo(name = "current_streak", defaultValue = "0")
    val currentStreak: Int = 0,

    @ColumnInfo(name = "best_streak", defaultValue = "0")
    val bestStreak: Int = 0,

    @ColumnInfo(name = "wallet", defaultValue = "0")
    val wallet: Int = 0
)



