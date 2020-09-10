package com.example.blackjack

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
}

class FinalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)


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
                    playerScore.text = "Player: ${playerSum}"
                    dealerScore.text = "Dealer: ${dealerSum}"
                }
                else if (outcome == "Bust!")
                {
                    playerScore.text = "Player: ${playerSum}"
                }
            }
            else
            {
                finalText.text = "Dealer: ${outcome}"
                dealerScore.text = "Player hand #1: ${playerSum}"
                playerScore.text = "Player hand #2: ${dealerSum}"
                if (Integer.valueOf(outcome.toString()) > Integer.valueOf(playerSum.toString()))
                {
                    Counter.dealerCnt++
                    Counter.onStreak = false
                    Counter.combo = 0
                }
                else if (Integer.valueOf(outcome.toString()) < Integer.valueOf(playerSum.toString()))
                {
                    if (!Counter.onStreak)
                    {
                        Counter.onStreak = true
                    }
                    Counter.combo++
                    Counter.playerCnt++
                }
                if (Integer.valueOf(outcome.toString()) > Integer.valueOf(dealerSum.toString()))
                {
                    Counter.dealerCnt++
                    Counter.onStreak = false
                    Counter.combo = 0
                }
                else if (Integer.valueOf(outcome.toString()) < Integer.valueOf(dealerSum.toString()))
                {
                    if (!Counter.onStreak)
                    {
                        Counter.onStreak = true
                    }
                    Counter.combo++
                    Counter.playerCnt++
                }
            }

            playerCount.text = "Player won: ${Counter.playerCnt}"
            dealerCount.text = "Dealer won: ${Counter.dealerCnt}"
            streakCounter.text = "Current Streak: ${Counter.combo}"
        }


        restartButton.setOnClickListener{
            val restartGame = Intent(applicationContext, MainActivity::class.java)
            startActivity(restartGame)
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        finalText.text = savedInstanceState.getString("final")
        playerScore.text = savedInstanceState.getString("playersc")
        dealerScore.text = savedInstanceState.getString("dealersc")

        playerCount.text = "Player won: ${Counter.playerCnt}"
        dealerCount.text = "Dealer won: ${Counter.dealerCnt}"
        streakCounter.text = "Current Streak: ${Counter.combo}"
    }
}

