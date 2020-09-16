package com.example.blackjack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT EXISTS(SELECT 1 FROM user_data WHERE username = :userName LIMIT 1)")
    suspend fun checkUser(userName: String) : Int

    @Query("SELECT userId FROM user_data WHERE username = :userName")
    suspend fun getUserId(userName: String) : Int

    @Query("SELECT best_streak FROM user_data WHERE userId = :userId")
    suspend fun getStreak(userId: Int) : Int

    @Query("UPDATE user_data SET wins = wins + 1 WHERE userId = :userId")
    suspend fun incWin(userId : Int)

    @Query("UPDATE user_data SET losses = losses + 1 WHERE userId = :userId")
    suspend fun incLoss(userId : Int)

    @Query("UPDATE user_data SET best_streak = :newStreak WHERE userId = :userId")
    suspend fun setStreak(userId: Int, newStreak: Int)
}


