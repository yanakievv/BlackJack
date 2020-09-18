package com.example.blackjack.statistics

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.blackjack.R
import com.example.blackjack.data.UserDAO
import com.example.blackjack.data.UserDatabase
import kotlinx.android.synthetic.main.activity_final.compareUser
import kotlinx.android.synthetic.main.activity_statistics.*
import kotlinx.coroutines.runBlocking

class User {
    private var uID: Int = 0
    var username: String = ""
    var wins: Int = 0
    var losses: Int = 0
    var splitHandsWon: Int = 0
    var splitHandsLost: Int = 0
    var doublesWon: Int = 0
    var currentStreak: Int = 0
    var bestStreak: Int = 0

    fun init(dbDAO: UserDAO, userName: String) {
        runBlocking {
            if (dbDAO.checkUser(userName) == 1) {
                uID = dbDAO.getUserId(userName)
                username = dbDAO.getUsername(uID)
                wins = dbDAO.getWins(uID)
                losses = dbDAO.getLosses(uID)
                splitHandsWon = dbDAO.getSplitWins(uID)
                splitHandsLost = dbDAO.getSplitLosses(uID)
                doublesWon = dbDAO.getDoublesWon(uID)
                currentStreak = dbDAO.getStreak(uID)
                bestStreak = dbDAO.getBestStreak(uID)
            }
            else
            {
                username = "Not Found."
            }
        }
    }
}

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val db = UserDatabase.getInstance(this)
        val dbDAO = db.userDAO
        val username = intent.getStringExtra("username")
        val user = User()
        user.init(dbDAO, username as String)
        showMainData(user)


        backButton.setOnClickListener{
            finish()
        }

        compareUser.setOnClickListener{
            val secondUser = User()
            secondUser.username = secondUsername.text.toString().capitalizeWords()
            secondUser.init(dbDAO, secondUser.username)
            showSecondaryData(secondUser)
        }

        overallStats.setOnClickListener{
            val overall = User()
            overall.init(dbDAO, "Overall")
            showSecondaryData(overall)
        }
    }

    @SuppressLint("SetTextI18n")
    fun showMainData(user: User) {
        username.text = user.username
        wins.text = "Wins: " + user.wins.toString()
        losses.text = "Losses: " + user.losses.toString()
        splitHandsWon.text = "Splits Won: " + user.splitHandsWon.toString()
        splitHandsLost.text = "Splits Lost: " + user.splitHandsLost.toString()
        doublesWon.text = "Doubles Won: " + user.doublesWon.toString()
        currentStreak.text = "Streak: " + user.currentStreak.toString()
        bestStreak.text = "Best Streak: " + user.bestStreak.toString()
    }
    @SuppressLint("SetTextI18n")
    fun showSecondaryData(user: User) {
        if (user.username != "Not Found.") {

            usernameSecondary.text = user.username
            winsSecondary.text = "Wins: " + user.wins.toString()
            lossesSecondary.text = "Losses: " + user.losses.toString()
            splitHandsWonSecondary.text = "Splits Won: " + user.splitHandsWon.toString()
            splitHandsLostSecondary.text = "Splits Lost: " + user.splitHandsLost.toString()
            doublesWonSecondary.text = "Doubles Won: " + user.doublesWon.toString()

            if (user.username != "Overall") {

                currentStreakSecondary.text = "Streak: " + user.currentStreak.toString()
                bestStreakSecondary.text = "Best Streak: " + user.bestStreak.toString()

                colourTexts(wins, winsSecondary, false)
                colourTexts(losses, lossesSecondary, true)
                colourTexts(splitHandsWon, splitHandsWonSecondary, false)
                colourTexts(splitHandsLost, splitHandsLostSecondary, true)
                colourTexts(doublesWon, doublesWonSecondary, false)
                colourTexts(currentStreak, currentStreakSecondary, false)
                colourTexts(bestStreak, bestStreakSecondary, false)

            } else {
                restoreColours()
                currentStreakSecondary.text = ""
                bestStreakSecondary.text = ""
            }
        }
        else
        {
            usernameSecondary.text = "Not Found."
            winsSecondary.text = ""
            lossesSecondary.text = ""
            splitHandsWonSecondary.text = ""
            splitHandsLostSecondary.text = ""
            doublesWonSecondary.text = ""
            currentStreakSecondary.text = ""
            bestStreakSecondary.text = ""

            restoreColours()
        }
    }

    fun restoreColours()
    {
        wins.setTextColor(Color.parseColor("#FFFFFF"))
        losses.setTextColor(Color.parseColor("#FFFFFF"))
        splitHandsWon.setTextColor(Color.parseColor("#FFFFFF"))
        splitHandsLost.setTextColor(Color.parseColor("#FFFFFF"))
        doublesWon.setTextColor(Color.parseColor("#FFFFFF"))
        currentStreak.setTextColor(Color.parseColor("#FFFFFF"))
        bestStreak.setTextColor(Color.parseColor("#FFFFFF"))

        winsSecondary.setTextColor(Color.parseColor("#FFFFFF"))
        lossesSecondary.setTextColor(Color.parseColor("#FFFFFF"))
        splitHandsWonSecondary.setTextColor(Color.parseColor("#FFFFFF"))
        splitHandsLostSecondary.setTextColor(Color.parseColor("#FFFFFF"))
        doublesWonSecondary.setTextColor(Color.parseColor("#FFFFFF"))
        currentStreakSecondary.setTextColor(Color.parseColor("#FFFFFF"))
        bestStreakSecondary.setTextColor(Color.parseColor("#FFFFFF"))
    }

    fun colourTexts(left: TextView, right: TextView, reverse: Boolean) {
        if (Integer.valueOf(left.text.takeLast(1).toString()) >= Integer.valueOf(right.text.takeLast(1).toString()) && !reverse) {
            if (Integer.valueOf(left.text.takeLast(1).toString()) != Integer.valueOf(right.text.takeLast(1).toString())) {
                left.setTextColor(Color.parseColor("#32CD32"))
                right.setTextColor(Color.parseColor("#FF0000"))
            }
            else {
                left.setTextColor(Color.parseColor("#FFFFFF"))
                right.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
        else if (!reverse) {
            left.setTextColor(Color.parseColor("#FF0000"))
            right.setTextColor(Color.parseColor("#32CD32"))
        }
        else if (Integer.valueOf(left.text.takeLast(1).toString()) >= Integer.valueOf(right.text.takeLast(1).toString()) && reverse) {
            if (Integer.valueOf(left.text.takeLast(1).toString()) != Integer.valueOf(right.text.takeLast(1).toString())) {
                left.setTextColor(Color.parseColor("#FF0000"))
                right.setTextColor(Color.parseColor("#32CD32"))
            }
            else {
                left.setTextColor(Color.parseColor("#FFFFFF"))
                right.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
        else if (reverse) {
            left.setTextColor(Color.parseColor("#32CD32"))
            right.setTextColor(Color.parseColor("#FF0000"))
        }
    }

    @SuppressLint("DefaultLocale")
    fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")
}