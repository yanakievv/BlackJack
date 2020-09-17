package com.example.blackjack.statistics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blackjack.R
import kotlinx.android.synthetic.main.activity_final.changeUser
import kotlinx.android.synthetic.main.activity_statistics.*

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        //TODO fetch player statistics from database
        //TODO add overall game statistics to database

        backButton.setOnClickListener{
            finish()
        }

        changeUser.setOnClickListener{
            //TODO change which user data the view is showing
        }
    }
}