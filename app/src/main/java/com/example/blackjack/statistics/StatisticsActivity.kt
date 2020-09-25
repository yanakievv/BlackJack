package com.example.blackjack.statistics

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.blackjack.R
import com.example.blackjack.data.User
import com.example.blackjack.data.UserDatabase
import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_statistics.*
import kotlinx.coroutines.runBlocking


class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        secondProfilePic.visibility = View.INVISIBLE

        nextRecord.visibility = View.INVISIBLE
        previousRecord.visibility = View.INVISIBLE

        val db = UserDatabase.getInstance(this)
        val dbDAO = db.userDAO
        val fbID: String
        if (Profile.getCurrentProfile() != null) {
            fbID = Profile.getCurrentProfile().id
            profilePic.visibility = View.VISIBLE
            profilePic.profileId = Profile.getCurrentProfile().id
        }
        else {
            fbID = GoogleSignIn.getLastSignedInAccount(this)!!.id as String
            profilePic.removeAllViews()
            secondProfilePic.removeAllViews()

        }
        val user: User
        runBlocking { user = dbDAO.getUserByFbID(fbID) }
        showMainData(user)


        backButton.setOnClickListener{
            finish()
        }

        compareUser.setOnClickListener{
            var index = 0
            var users: Array<User>
            runBlocking { users = dbDAO.getAllUsername(fbID,"%" + secondUsername.text.toString() + "%") }
            if (users.isEmpty()) {
                invalidUser()
            }
            else {
                secondProfilePic.visibility = View.VISIBLE
                secondProfilePic.profileId = users[index].accID
                showSecondaryData(users[index])

            }

            if (users.size == 1 || users.isEmpty()) {
                nextRecord.visibility = View.INVISIBLE

            }
            else {
                nextRecord.visibility = View.VISIBLE
            }

            nextRecord.setOnClickListener{
                if (users.isNotEmpty() && index + 1 < users.size) {
                    previousRecord.visibility = View.VISIBLE
                    showSecondaryData(users[++index])
                    secondProfilePic.profileId = users[index].accID
                    if (index == users.size - 1) {
                        nextRecord.visibility = View.INVISIBLE
                    }
                }
            }

            previousRecord.setOnClickListener{
                if (users.isNotEmpty() && index - 1 >= 0) {
                    nextRecord.visibility = View.VISIBLE
                    showSecondaryData(users[--index])
                    secondProfilePic.profileId = users[index].accID
                    if (index == 0) {
                        previousRecord.visibility = View.INVISIBLE
                    }
                }
            }
        }

        overallStats.setOnClickListener{
            nextRecord.visibility = View.GONE
            previousRecord.visibility = View.GONE
            val overall: User
            runBlocking { overall = dbDAO.getUserByFbID("Overall") }
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

                nextRecord.visibility = View.GONE
                previousRecord.visibility = View.GONE
                secondProfilePic.visibility = View.GONE

                restoreColours()
                currentStreakSecondary.text = ""
                bestStreakSecondary.text = ""
            }
        }
        else
        {
            invalidUser()
        }
    }

    @SuppressLint("SetTextI18n")
    fun invalidUser() {
        nextRecord.visibility = View.GONE
        previousRecord.visibility = View.GONE
        secondProfilePic.visibility = View.GONE

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
        if (Integer.valueOf(left.text.takeLast(1).toString()) >= Integer.valueOf(
                right.text.takeLast(
                    1
                ).toString()
            ) && !reverse) {
            if (Integer.valueOf(left.text.takeLast(1).toString()) != Integer.valueOf(
                    right.text.takeLast(
                        1
                    ).toString()
                )) {
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
        else if (Integer.valueOf(left.text.takeLast(1).toString()) >= Integer.valueOf(
                right.text.takeLast(
                    1
                ).toString()
            ) && reverse) {
            if (Integer.valueOf(left.text.takeLast(1).toString()) != Integer.valueOf(
                    right.text.takeLast(
                        1
                    ).toString()
                )) {
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
}