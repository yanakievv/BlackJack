package com.example.blackjack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        startButton.setOnClickListener{
            val startGame = Intent(applicationContext, MainActivity::class.java)
            startActivity(startGame)
        }
    }
}






