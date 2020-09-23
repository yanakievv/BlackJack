package com.example.blackjack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT EXISTS(SELECT 1 FROM user_data WHERE fb_ID = :fbID LIMIT 1)")
    suspend fun checkUser(fbID: String) : Int

    @Query("SELECT userId FROM user_data WHERE fb_ID = :fbID")
    suspend fun getUserId(fbID: String) : Int

    @Query("SELECT username FROM user_data WHERE userId = :userId")
    suspend fun getUsername(userId: Int) : String

    @Query("SELECT current_streak FROM user_data WHERE userId = :userId")
    suspend fun getStreak(userId: Int) : Int

    @Query("SELECT best_streak FROM user_data WHERE userId = :userId")
    suspend fun getBestStreak(userId: Int) : Int

    @Query("SELECT wins FROM user_data WHERE userId = :userId")
    suspend fun getWins(userId: Int) : Int

    @Query("SELECT losses FROM user_data WHERE userId = :userId")
    suspend fun getLosses(userId: Int) : Int

    @Query("SELECT split_hands_won FROM user_data WHERE userId = :userId")
    suspend fun getSplitWins(userId: Int) : Int

    @Query("SELECT split_hands_lost FROM user_data WHERE userId = :userId")
    suspend fun getSplitLosses(userId: Int) : Int

    @Query("SELECT doubles_won FROM user_data WHERE userId = :userId")
    suspend fun getDoublesWon(userId: Int) : Int

    @Query("SELECT * FROM user_data WHERE username = :username")
    suspend fun getAllUsername(username: String) : Array<User>

    @Query("UPDATE user_data SET wins = wins + 1 WHERE userId = :userId")
    suspend fun incWin(userId : Int)

    @Query("UPDATE user_data SET losses = losses + 1 WHERE userId = :userId")
    suspend fun incLoss(userId : Int)

    @Query("UPDATE user_data SET current_streak = :newStreak WHERE userId = :userId")
    suspend fun setStreak(userId: Int, newStreak: Int)

    @Query("UPDATE user_data SET best_streak = :newStreak WHERE userId = :userId")
    suspend fun setBestStreak(userId: Int, newStreak: Int)

    @Query("UPDATE user_data SET split_hands_won = split_hands_won + 1 WHERE userId = :userId")
    suspend fun incSplitWin(userId : Int)

    @Query("UPDATE user_data SET split_hands_lost = split_hands_lost + 1 WHERE userId = :userId")
    suspend fun incSplitLoss(userId : Int)

    @Query("UPDATE user_data SET doubles_won = doubles_won + 1 WHERE userId = :userId")
    suspend fun incDoubleWon(userId : Int)

    @Query("DELETE FROM user_data WHERE userId = :userId")
    suspend fun removeEntry(userId: Int)

    @Query("DELETE FROM user_data")
    suspend fun removeAll()
}


