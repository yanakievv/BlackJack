package com.example.blackjack.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    @ColumnInfo(name = "fb_ID")
    val fbID: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "wins")
    val wins: Int = 0,

    @ColumnInfo(name = "losses")
    val losses: Int = 0,

    @ColumnInfo(name = "split_hands_won")
    val splitHandsWon: Int = 0,

    @ColumnInfo(name = "split_hands_lost")
    val splitHandsLost: Int = 0,

    @ColumnInfo(name = "doubles_won")
    val doublesWon: Int = 0,

    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,

    @ColumnInfo(name = "best_streak")
    val bestStreak: Int = 0
)



