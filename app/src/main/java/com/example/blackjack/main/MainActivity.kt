package com.example.blackjack.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blackjack.R
import com.example.blackjack.contract.Contract
import com.example.blackjack.final.FinalActivity
import com.facebook.AccessToken
import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

internal lateinit var presenter: Contract.MainActivityPresenter
internal var lastBetVal = 0


class MainActivity : AppCompatActivity(), View.OnClickListener, Contract.MainView {


    private var dealerHand = mutableListOf<Card>()
    private var dealerView = mutableListOf<TextView>()

    private var playerHand = mutableListOf<Card>()
    private var playerSplitHand = mutableListOf<Card>()
    private var playerView = mutableListOf<TextView>()
    private var playerSplitView = mutableListOf<TextView>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toFinal.setOnClickListener(this)
        buttonHit.setOnClickListener(this)
        buttonPass.setOnClickListener(this)
        buttonDouble.setOnClickListener(this)
        buttonSplit.setOnClickListener(this)

        toFinal.visibility = View.INVISIBLE

        setPresenter(MainActivityPresenter(this))

        presenter.connect(this)
        setBets()
        //presenter.init(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            //R.id.toFinal -> startActivity(Intent(this, FinalActivity::class.java))
            R.id.buttonHit -> presenter.hitAction()
            R.id.buttonPass -> presenter.standAction()
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

        buttonHit.visibility = View.VISIBLE
        buttonPass.visibility = View.VISIBLE
        buttonDouble.visibility = View.VISIBLE
        buttonSplit.visibility = View.VISIBLE

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

    override fun bust(doubled: String) {
        Toast.makeText(this, "Bust!", Toast.LENGTH_SHORT).show()
        finalizeActivity("Bust!", double = doubled)
    }

    override fun win(doubled: String) {
        finalizeActivity("Winner!", double = doubled)
    }

    override fun loss(doubled: String) {
        finalizeActivity("Dealer won.", double = doubled)
    }

    override fun twentyOne() {
        Toast.makeText(this, "BlackJack!", Toast.LENGTH_SHORT).show()
        finalizeActivity("BlackJack!")
    }

    override fun tie() {
        finalizeActivity("Tied.")
    }

    override fun split(doubled: String, secondDouble: String) {
        finalizeActivity(split = "t", double = doubled, secondDouble = secondDouble)
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

    private fun finalizeActivity(outcome: String = "", split: String = "f", double: String = "f", secondDouble: String = "f") {

        //toFinal.visibility = View.VISIBLE
        val finalIntent = Intent(this, FinalActivity::class.java)
        finalIntent.putExtra("double", double)
        finalIntent.putExtra("secondDouble", secondDouble)
        finalIntent.putExtra("split", split)
        finalIntent.putExtra("bet", presenter.getBet())

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

    private fun setBets() {

        disableButtons()
        var uID = ""
        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            profilePic.visibility = View.VISIBLE
            profilePic.profileId = Profile.getCurrentProfile().id
            uID = Profile.getCurrentProfile().id
        }
        else if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            profilePic.visibility = View.INVISIBLE

            uID = GoogleSignIn.getLastSignedInAccount(this)!!.id as String
        }
        else {
            profilePic.visibility = View.INVISIBLE
            confirmBet.visibility = View.INVISIBLE
            editBet.visibility = View.INVISIBLE
            lastBet.visibility = View.INVISIBLE
            presenter.init(this)
        }

        confirmBet.setOnClickListener {
            if (editBet.text.toString() == "" || editBet.text.toString().toIntOrNull() == null) {
                Toast.makeText(this, "Enter a valid bet.", Toast.LENGTH_SHORT).show()
            }
            else {
                val betSuccessful = presenter.setBet(uID, Integer.valueOf(editBet.text.toString()), this)
                if (!betSuccessful) {
                    Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show()
                }
                else {
                    confirmBet.visibility = View.INVISIBLE
                    editBet.visibility = View.INVISIBLE
                    lastBet.visibility = View.INVISIBLE
                    lastBetVal = editBet.text.toString().toInt()

                    presenter.init(this)
                }
            }
        }

        lastBet.setOnClickListener {
            editBet.setText(lastBetVal.toString())
        }
    }
}