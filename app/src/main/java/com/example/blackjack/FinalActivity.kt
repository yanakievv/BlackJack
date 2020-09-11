package com.example.blackjack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_final.*

object Counter
{
    var playerCnt: Int = 0
    var dealerCnt: Int = 0
    var combo: Int = 0
    var onStreak: Boolean = false
    var userName: String = "Player"

    fun reset()
    {
        playerCnt = 0
        dealerCnt = 0
        combo = 0
        onStreak = false
    }
}

class FinalActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)

        Counter.userName = intent.getStringExtra("username") as String

        if (savedInstanceState == null)
        {
            val outcome = intent.getStringExtra("outcome")
            val playerSum = intent.getStringExtra("player")
            val dealerSum = intent.getStringExtra("dealer")

            if (outcome == "BlackJack!" || outcome == "Winner!")
            {
                if (!Counter.onStreak)
                {
                    Counter.onStreak = true
                }
                Counter.combo++
                Counter.playerCnt++
            }
            else if (outcome != "Tied." && intent.getStringExtra("split") == "f")
            {
                Counter.dealerCnt++
                Counter.onStreak = false
                Counter.combo = 0
            }

            if (intent.getStringExtra("split") == "f")
            {
                finalText.text = outcome
                if (outcome != "BlackJack!" && outcome != "Bust!")
                {
                    playerScore.text = "${Counter.userName}: ${playerSum}"
                    dealerScore.text = "Dealer: ${dealerSum}"
                }
                else if (outcome == "Bust!")
                {
                    playerScore.text = "${Counter.userName}: ${playerSum}"
                }
            }
            else
            {
                finalText.text = "Dealer: ${outcome}"
                playerScore.text = "${Counter.userName} hand #2: ${dealerSum}"
                dealerScore.text = "${Counter.userName} hand #1: ${playerSum}"
                if ((Integer.valueOf(outcome.toString()) > Integer.valueOf(playerSum.toString()) && (Integer.valueOf(outcome.toString()) <= 21)) || (Integer.valueOf(playerSum.toString()) > 21))
                {
                    Counter.dealerCnt++
                    Counter.onStreak = false
                    Counter.combo = 0
                }
                else if ((Integer.valueOf(outcome.toString()) < Integer.valueOf(playerSum.toString()) && (Integer.valueOf(playerSum.toString()) <= 21)) || (Integer.valueOf(outcome.toString()) > 21))
                {
                    if (!Counter.onStreak)
                    {
                        Counter.onStreak = true
                    }
                    Counter.combo++
                    Counter.playerCnt++
                }
                if ((Integer.valueOf(outcome.toString()) > Integer.valueOf(dealerSum.toString()) && (Integer.valueOf(outcome.toString()) <= 21)) || (Integer.valueOf(playerSum.toString()) > 21))
                {
                    Counter.dealerCnt++
                    Counter.onStreak = false
                    Counter.combo = 0
                }
                else if ((Integer.valueOf(outcome.toString()) < Integer.valueOf(dealerSum.toString()) && (Integer.valueOf(dealerSum.toString()) <= 21)) || (Integer.valueOf(outcome.toString()) > 21))
                {
                    if (!Counter.onStreak)
                    {
                        Counter.onStreak = true
                    }
                    Counter.combo++
                    Counter.playerCnt++
                }
            }

            playerCount.text = "${Counter.userName} won: ${Counter.playerCnt}"
            dealerCount.text = "Dealer won: ${Counter.dealerCnt}"
            streakCounter.text = "Current Streak: ${Counter.combo}"
        }


        restartButton.setOnClickListener{
            val restartGame = Intent(applicationContext, MainActivity::class.java)
            restartGame.putExtra("username", Counter.userName)
            startActivity(restartGame)
        }

        changeUser.setOnClickListener{
            Counter.reset()
            val changePlayer = Intent(applicationContext, PlayActivity::class.java)
            startActivity(changePlayer)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString("final", finalText.text.toString())
            putString("playersc", playerScore.text.toString())
            putString("dealersc", dealerScore.text.toString())
        }
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("SetTextI18n")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        finalText.text = savedInstanceState.getString("final")
        playerScore.text = savedInstanceState.getString("playersc")
        dealerScore.text = savedInstanceState.getString("dealersc")

        playerCount.text = "${Counter.userName} won: ${Counter.playerCnt}"
        dealerCount.text = "Dealer won: ${Counter.dealerCnt}"
        streakCounter.text = "Current Streak: ${Counter.combo}"
    }


}

