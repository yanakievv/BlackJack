package com.example.blackjack.final

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blackjack.R
import com.example.blackjack.contract.Contract
import com.example.blackjack.main.MainActivity
import com.example.blackjack.startup.LoginActivity
import com.example.blackjack.statistics.StatisticsActivity
import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_final.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputFromMain(var fbID: String?, var username: String?, var split: String?, var outcome: String?, var player: String?, var dealer: String?, var double: String?)



internal lateinit var presenter: Contract.FinalActivityPresenter

class FinalActivity : AppCompatActivity(), Contract.FinalView {

    private var input: InputFromMain? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        Stetho.initializeWithDefaults(this) // for looking up the data live while using the app in chrome://inspect on PC's browser

        if (savedInstanceState == null) {
            val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
            if (accessToken != null && !accessToken.isExpired) {
                setPresenter(FinalActivityPresenter(this))
                presenter.connect(this)

                input = InputFromMain(
                    fbID = Profile.getCurrentProfile().id,
                    username = Profile.getCurrentProfile().firstName,
                    split = intent.getStringExtra("split"),
                    outcome = intent.getStringExtra("outcome"),
                    player = intent.getStringExtra("player"),
                    dealer = intent.getStringExtra("dealer"),
                    double = intent.getStringExtra("double")
                )


                CoroutineScope(Dispatchers.IO).launch {
                    presenter.process(input as InputFromMain)
                }
            }
            else {
                input = InputFromMain(
                    fbID = null,
                    username = "Player",
                    split = intent.getStringExtra("split"),
                    outcome = intent.getStringExtra("outcome"),
                    player = intent.getStringExtra("player"),
                    dealer = intent.getStringExtra("dealer"),
                    double = intent.getStringExtra("double")
                )
            }
            if (input?.split == "f") {
                when (input?.outcome) {
                    "Bust!" -> bust()
                    "BlackJack!" -> twentyOne()
                    else -> regular()
                }
            }
            else split()
        }

        // Removed views for wins, losses and streaks because of synchronization problems.
        // Because the "presenter.process()" function is suspendable(as well as all database access functions) and runs in a new coroutine the views get old or inconsistent information so all of that will be moved into a separate activity(StatisticsActivity).
        // Could have used "runBlocking" instead or declared the functions without the "suspend" keyword, but that'll block the current thread, slowing down the UI when querying a "fatter" database.

        restartButton.setOnClickListener{
            val restartGame = Intent(applicationContext, MainActivity::class.java)
            startActivity(restartGame)
        }

        changeUser.setOnClickListener{
            val changePlayer = Intent(applicationContext, LoginActivity::class.java)
            startActivity(changePlayer)
        }
        statistics.setOnClickListener{
            val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
            if (accessToken != null && !accessToken.isExpired) {
                val statistics = Intent(applicationContext, StatisticsActivity::class.java)
                statistics.putExtra("username", input?.username)
                startActivity(statistics)
            }
            else {
                Toast.makeText(this, "You must be logged in to access statistics.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    @SuppressLint("SetTextI18n")
    fun regular() {
        finalText.text = input?.outcome
        playerScore.text = "${input?.username}: ${input?.player}"
        dealerScore.text = "Dealer: ${input?.dealer}"

    }

    @SuppressLint("SetTextI18n")
    fun bust() {
        finalText.text = "Bust!"
        playerScore.text = "${input?.username}: ${input?.player}"
    }

    @SuppressLint("SetTextI18n")
    fun twentyOne() {
        finalText.text = "BlackJack!"
    }

    @SuppressLint("SetTextI18n")
    fun split() {
        finalText.text = "Dealer: ${input?.outcome}"
        playerScore.text = "${input?.username} hand #2: ${input?.dealer}"
        dealerScore.text = "${input?.username} hand #1: ${input?.player}"
    }

    override fun setPresenter(presenter: Contract.FinalActivityPresenter) {
        com.example.blackjack.final.presenter = presenter
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString("final", finalText.text.toString())
            putString("player", playerScore.text.toString())
            putString("dealer", dealerScore.text.toString())
            putString("username", input?.username)
        }
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("SetTextI18n")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        finalText.text = savedInstanceState.getString("final")
        playerScore.text = savedInstanceState.getString("player")
        dealerScore.text = savedInstanceState.getString("dealer")
        input?.username = savedInstanceState.getString("username")
    }
}

