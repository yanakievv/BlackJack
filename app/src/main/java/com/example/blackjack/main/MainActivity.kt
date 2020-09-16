package com.example.blackjack.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blackjack.Contract
import com.example.blackjack.FinalActivity
import com.example.blackjack.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

internal lateinit var presenter: Contract.MainActivityPresenter

class MainActivity : AppCompatActivity(), View.OnClickListener, Contract.MainView {


    var dealerHand = mutableListOf<Card>()
    var dealerView = mutableListOf<TextView>()

    var playerHand = mutableListOf<Card>()
    var playerSplitHand = mutableListOf<Card>()
    var playerView = mutableListOf<TextView>()
    var playerSplitView = mutableListOf<TextView>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        buttonHit.setOnClickListener(this)
        buttonPass.setOnClickListener(this)
        buttonDouble.setOnClickListener(this)
        buttonSplit.setOnClickListener(this)

        setPresenter(MainActivityPresenter(this))


        presenter.init()

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonHit -> presenter.hitAction()
            R.id.buttonPass -> presenter.dealerTurn()
            R.id.buttonDouble -> presenter.doubleAction()
            R.id.buttonSplit -> presenter.splitAction()
        }
    }


    @SuppressLint("SetTextI18n")
    fun viewCards() {
        val dealerViewIt = dealerView.iterator()
        val playerViewIt = playerView.iterator()
        val playerSplitViewIt = playerSplitView.iterator()

        for (dealerHandIt in dealerHand) {
            dealerViewIt.next().text = dealerHandIt.ref + dealerHandIt.suit.type.toString()
        }
        for (playerHandIt in playerHand) {
            playerViewIt.next().text = playerHandIt.ref + playerHandIt.suit.type.toString()
        }
        for (playerSplitHandIt in playerSplitHand) {
            playerSplitViewIt.next().text = playerSplitHandIt.ref + playerSplitHandIt.suit.type.toString()
        }
    }

    override fun init() {

        presenter.setUsername(intent.getStringExtra("username").toString())
        if (presenter.getUsername() == "")
        {
            presenter.setUsername("Player")
        }

        dealerView.add(dealerCard1)
        dealerView.add(dealerCard2)
        dealerView.add(dealerCard3)
        dealerView.add(dealerCard4)
        dealerView.add(dealerCard5)


        playerView.add(playerCard1)
        playerView.add(playerCard2)
        playerView.add(playerCard3)
        playerView.add(playerCard4)
        playerView.add(playerCard5)
        playerView.add(playerCard6)

        playerSplitView.add(hand2card1)
        playerSplitView.add(hand2card2)
        playerSplitView.add(hand2card3)
        playerSplitView.add(hand2card4)
        playerSplitView.add(hand2card5)
        playerSplitView.add(hand2card6)

        refreshView()
    }

    override fun refreshView() {

        dealerHand = presenter.getDealerHand()
        playerHand = presenter.getPlayerHand()
        playerSplitHand = presenter.getPlayerSplitHand()

        viewCards()
    }

    override fun cleanUpView() {

        for (it in playerView) {
            it.text = ""
        }
        refreshView()
    }

    override fun setPresenter(presenter: Contract.MainActivityPresenter) {
        com.example.blackjack.main.presenter = presenter
    }

    override fun bust() {
        Toast.makeText(this, "Bust!", Toast.LENGTH_SHORT).show()
        finalizeActivity("Bust!")
    }

    override fun win() {
        finalizeActivity("Winner!")
    }

    override fun loss() {
        finalizeActivity("Dealer won.")
    }

    override fun twentyOne() {
        Toast.makeText(this, "BlackJack!", Toast.LENGTH_SHORT).show()
        finalizeActivity("BlackJack!")
    }

    override fun tie() {
        finalizeActivity("Tied.")
    }

    override fun split() {
        finalizeActivity(split = "t")
    }

    override fun disableButtons() {
        buttonHit.isClickable = false
        buttonDouble.isClickable = false
        buttonPass.isClickable = false
        buttonSplit.isClickable = false
    }
    override fun enableButtons() {
        buttonHit.isClickable = true
        buttonDouble.isClickable = true
        buttonPass.isClickable = true
        buttonSplit.isClickable = true
    }

    fun finalizeActivity(outcome: String = "", split: String = "f") {

        val finalIntent = Intent(this, FinalActivity::class.java)
        finalIntent.putExtra("split", split)
        finalIntent.putExtra("username", presenter.getUsername())

        if (split == "t") {
            finalIntent.putExtra("outcome", presenter.getDealerSum().toString())
            finalIntent.putExtra("player", presenter.getPlayerSplitSum().toString())
            finalIntent.putExtra("dealer", presenter.getPlayerSum().toString())
        }
        else {
            finalIntent.putExtra("outcome", outcome)
            finalIntent.putExtra("player", presenter.getPlayerSum().toString())
            finalIntent.putExtra("dealer", presenter.getDealerSum().toString())
        }

        Timer("pause", false).schedule(2000) {
            startActivity(finalIntent)
        }
    }


}