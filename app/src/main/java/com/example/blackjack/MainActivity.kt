package com.example.blackjack


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule



object Game {
    val cards = mutableListOf(
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        10,
        10,
        10,
        11,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        10,
        10,
        10,
        11,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        10,
        10,
        10,
        11,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        10,
        10,
        10,
        11
    )
    var playerSum: Int = 0
    var playerSplitSum: Int = 0
    var dealerSum: Int = 0

    var it = cards.iterator()
    var dealerArr = mutableListOf<TextView>()
    var playerArr = mutableListOf<TextView>()

    var dealerIndex: Int = 0
    var playerIndex: Int = 0


    var playerAce: Boolean = false
    var dealerAce: Boolean = false

    var hasSplit: Boolean = false
    var hasHadSplit: Boolean = false

    var userName: String = "Player"

    var dealerHadTurn: Boolean = false
}


class MainActivity : AppCompatActivity(), View.OnClickListener {

    fun initSplit()
    {
        Game.playerArr.clear()
        Game.hasSplit = true
        Game.hasHadSplit = true
        Game.playerSum /= 2
        Game.playerSplitSum = Game.playerSum


        Game.playerArr.add(hand1card2)
        Game.playerArr.add(hand1card3)
        Game.playerArr.add(hand1card4)

        Game.playerArr.add(hand2card2)
        Game.playerArr.add(hand2card3)
        Game.playerArr.add(hand2card4)


    }

    fun hitAction()
    {
        if (Game.playerIndex == Game.playerArr.size - 1)
        {
            dealerTurn()
        }

        val newCard: Int = Game.it.next()

        if (newCard == 11)
        {
            Game.playerAce = true
        }

        Game.playerArr[Game.playerIndex].text = newCard.toString()

        Game.playerIndex++

        Thread.sleep(500)

        Game.playerSum += newCard
        if (Game.playerSum > 21 && !Game.playerAce)
        {
            if (Game.hasSplit)
            {
                Toast.makeText(this, "Hand #1: Bust!",Toast.LENGTH_SHORT).show()
                Game.hasSplit = false
                Game.playerSplitSum = Game.playerSum
                Game.playerIndex = 3
                Game.playerSum = Integer.valueOf(hand2card1.text.toString())
                hitAction()
            }
            else if (Game.hasHadSplit && Game.playerSplitSum <= 21)
            {
                dealerTurn()
            }
            else
            {
                Toast.makeText(this, "Bust!",Toast.LENGTH_SHORT).show()
                val finalIntent = Intent(this, FinalActivity::class.java)
                finalIntent.putExtra("username", Game.userName)
                finalIntent.putExtra("outcome","Bust!")
                finalIntent.putExtra("player", Game.playerSum.toString())
                finalIntent.putExtra("dealer", Game.dealerSum.toString())
                finalIntent.putExtra("split", "f")
                Timer("pause", false).schedule(2000) {
                    startActivity(finalIntent)
                }
            }
        }
        else if (Game.playerSum > 21 && Game.playerAce)
        {
            Game.playerSum -= 10
            Game.playerAce = false
            if (Integer.valueOf(playerCard1.text.toString()) == 11)
            {
                playerCard1.text = "1"
            }
            else if (Integer.valueOf(playerCard2.text.toString()) == 11)
            {
                playerCard2.text = "1"
            }
            else
            {
                for (i in Game.playerArr)
                {
                    if (Integer.valueOf(i.text.toString()) == 11)
                    {
                        i.text = "1"
                        break
                    }
                }
            }
        }
        else if (Game.playerSum == 21)
        {
            if (Game.hasSplit)
            {
                Toast.makeText(this, "Hand #1: 21!",Toast.LENGTH_SHORT).show()
                Game.hasSplit = false
                Game.playerSplitSum = Game.playerSum
                Game.playerIndex = 3
                Game.playerSum = Integer.valueOf(hand2card1.text.toString())
                hitAction()

            }
            else if (Game.hasHadSplit)
            {
                Toast.makeText(this, "Hand #2: 21!",Toast.LENGTH_SHORT).show()
                dealerTurn()
            }
            else
            {
                Toast.makeText(this, "21!",Toast.LENGTH_SHORT).show()
                dealerTurn()
            }
        }

    }

    fun dealerTurn()
    {
        if (!Game.hasHadSplit && Game.playerSum > 21)
        {
            return
        }
        if (!Game.hasSplit)
        {
            if (Game.dealerHadTurn)
            {
                return
            }
            else
            {
                Game.dealerHadTurn = true
            }
            dealerCard1.text = Game.it.next().toString()
            if (Integer.valueOf(dealerCard1.text.toString()) == 11)
            {
                Game.dealerAce = true
            }
            Game.dealerSum += Integer.valueOf(dealerCard1.text.toString())


            if (Integer.valueOf(dealerCard1.text.toString()) == 11 && Game.dealerAce)
            {
                Game.dealerSum -= 10
            }

            while (Game.dealerSum < 17)
            {
                val tempCard: Int = Game.it.next()

                if (tempCard == 11)
                {
                    Game.dealerAce = true
                }

                Game.dealerArr[Game.dealerIndex].text = tempCard.toString()

                Game.dealerSum += tempCard
                Game.dealerIndex++

                if (Game.dealerSum > 21 && Game.dealerAce)
                {
                    Game.dealerSum -= 10
                    Game.dealerAce = false

                    if (Integer.valueOf(dealerCard1.text.toString()) == 11)
                    {
                        dealerCard1.text = "1"
                    }
                    else if (Integer.valueOf(dealerCard2.text.toString()) == 11)
                    {
                        dealerCard2.text = "1"
                    }
                    else
                    {
                        for (i in Game.dealerArr)
                        {
                            if (Integer.valueOf(i.text.toString()) == 11)
                            {
                                i.text = "1"
                                break
                            }
                        }
                    }
                }

            }

            val finalIntent = Intent(this, FinalActivity::class.java)

            if (Game.hasHadSplit)
            {
                finalIntent.putExtra("split", "t")
                finalIntent.putExtra("username", Game.userName)
                finalIntent.putExtra("outcome", Game.dealerSum.toString())
                finalIntent.putExtra("player", Game.playerSplitSum.toString())
                finalIntent.putExtra("dealer", Game.playerSum.toString())

                if ((Game.dealerSum > 21 || Game.playerSplitSum > Game.dealerSum) && Game.playerSplitSum <= 21)
                {
                    Toast.makeText(this, "Hand #1: Win!", Toast.LENGTH_SHORT).show()
                }
                else if (Game.dealerSum > Game.playerSplitSum || Game.playerSplitSum > 21)
                {
                    Toast.makeText(this, "Hand #1: Loss.", Toast.LENGTH_SHORT).show()
                }
                else if (Game.dealerSum == Game.playerSplitSum)
                {
                    Toast.makeText(this, "Hand #1: Tie.", Toast.LENGTH_SHORT).show()
                }

                if ((Game.dealerSum > 21 || Game.playerSum > Game.dealerSum) && Game.playerSum <= 21)
                {
                    Toast.makeText(this, "Hand #2: Win!",Toast.LENGTH_SHORT).show()
                }
                else if (Game.dealerSum > Game.playerSum || Game.playerSum > 21)
                {
                    Toast.makeText(this, "Hand #2: Loss.", Toast.LENGTH_SHORT).show()
                }
                else if (Game.dealerSum == Game.playerSum)
                {
                    Toast.makeText(this, "Hand #2: Tie.", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                finalIntent.putExtra("username", Game.userName)
                finalIntent.putExtra("player", Game.playerSum.toString())
                finalIntent.putExtra("dealer", Game.dealerSum.toString())
                finalIntent.putExtra("split", "f")

                if ((Game.dealerSum > 21 || Game.playerSum > Game.dealerSum) && Game.playerSum <= 21)
                {
                    Toast.makeText(this, "You win!",Toast.LENGTH_SHORT).show()
                    finalIntent.putExtra("outcome","Winner!")

                }
                else if (Game.dealerSum > Game.playerSum || Game.playerSum > 21)
                {
                    Toast.makeText(this, "You lose.", Toast.LENGTH_SHORT).show()
                    finalIntent.putExtra("outcome","Dealer won.")
                }
                else
                {
                    Toast.makeText(this, "Tie.", Toast.LENGTH_SHORT).show()
                    finalIntent.putExtra("outcome","Tied.")
                }
            }

            Timer("pause", false).schedule(2000) {
                startActivity(finalIntent)
            }


        }
        else
        {
            Game.hasSplit = false
            Game.playerSplitSum = Game.playerSum
            Game.playerSum = Integer.valueOf(hand2card1.text.toString())
            Game.playerIndex = 3
            hitAction()
        }


    }

    fun doubleAction()
    {
        if (Game.playerIndex == 0 || (Game.hasHadSplit && (Game.playerIndex == 1 || Game.playerIndex == 4)))
        {
            hitAction()
            dealerTurn()


        }
    }

    @SuppressLint("SetTextI18n")
    fun splitAction()
    {
        if (Game.playerIndex == 0 && Integer.valueOf(playerCard1.text.toString()) == Game.playerSum / 2)
        {
            hand1.text = "Hand #1"
            hand2.text = "Hand #2"
            initSplit()

            hand1card1.text = playerCard1.text
            hand2card1.text = playerCard2.text
            playerCard1.text = ""
            playerCard2.text = ""

            hitAction()

        }
    }


    fun initGame()
    {
        Game.cards.shuffle()
        Game.dealerHadTurn = false
        Game.userName = intent.getStringExtra("username") as String
        if (Game.userName == "")
        {
            Game.userName = "Player"
        }

        Game.it = Game.cards.iterator()

        Game.playerSum = 0
        Game.dealerSum = 0

        Game.hasHadSplit = false
        Game.hasSplit = false

        Game.playerArr.clear()
        Game.dealerArr.clear()

        Game.playerAce = false
        Game.dealerAce = false

        Game.playerIndex = 0
        Game.dealerIndex = 0

        Game.dealerArr.add(dealerCard3)
        Game.dealerArr.add(dealerCard4)
        Game.dealerArr.add(dealerCard5)

        Game.playerArr.add(playerCard3)
        Game.playerArr.add(playerCard4)
        Game.playerArr.add(playerCard5)

        dealerCard1.text = "H"
        playerCard1.text = Game.it.next().toString()
        dealerCard2.text = Game.it.next().toString()
        playerCard2.text = Game.it.next().toString()

        if (Integer.valueOf(dealerCard2.text.toString()) == 11)
        {
            Game.dealerAce = true
        }

        if ((Integer.valueOf(playerCard1.text.toString()) == 11) || (Integer.valueOf(playerCard2.text.toString()) == 11))
        {
            Game.playerAce = true
        }


        Game.dealerSum += Integer.valueOf(dealerCard2.text.toString())
        Game.playerSum += Integer.valueOf(playerCard1.text.toString())
        Game.playerSum += Integer.valueOf(playerCard2.text.toString())

        if (Game.playerSum == 21)
        {
            Toast.makeText(this, "Blackjack!", Toast.LENGTH_SHORT).show()
            val finalIntent = Intent(this, FinalActivity::class.java)
            finalIntent.putExtra("username", Game.userName)
            finalIntent.putExtra("outcome","BlackJack!")
            finalIntent.putExtra("player", "")
            finalIntent.putExtra("dealer","")
            finalIntent.putExtra("split", "f")
            Timer("pause", false).schedule(2000) {
                startActivity(finalIntent)
            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonHit.setOnClickListener(this)
        buttonPass.setOnClickListener(this)
        buttonDouble.setOnClickListener(this)
        buttonSplit.setOnClickListener(this)

        if (savedInstanceState == null)
        {
            initGame()
        }



    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonHit -> hitAction()
            R.id.buttonPass -> dealerTurn()
            R.id.buttonDouble -> doubleAction()
            R.id.buttonSplit -> splitAction()

            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {

            if (!Game.hasHadSplit)
            {
                putString("player1", playerCard1.text.toString())
                putString("player2", playerCard2.text.toString())
                putString("player3", playerCard3.text.toString())
                putString("player4", playerCard4.text.toString())
                putString("player5", playerCard5.text.toString())
            }
            else
            {
                putString("splith1", hand1.text.toString())
                putString("splith2", hand2.text.toString())

                putString("splith1c1", hand1card1.text.toString())
                putString("splith1c2", hand1card2.text.toString())
                putString("splith1c3", hand1card3.text.toString())
                putString("splith1c4", hand1card4.text.toString())

                putString("splith2c1", hand2card1.text.toString())
                putString("splith2c2", hand2card2.text.toString())
                putString("splith2c3", hand2card3.text.toString())
                putString("splith2c4", hand2card4.text.toString())
            }


            putString("dealer1", dealerCard1.text.toString())
            putString("dealer2", dealerCard2.text.toString())
            putString("dealer3", dealerCard3.text.toString())
            putString("dealer4", dealerCard4.text.toString())
            putString("dealer5", dealerCard5.text.toString())


        }

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        Game.playerArr.clear()
        Game.dealerArr.clear()

        Game.dealerArr.add(dealerCard3)
        Game.dealerArr.add(dealerCard4)
        Game.dealerArr.add(dealerCard5)

        Game.playerArr.add(playerCard3)
        Game.playerArr.add(playerCard4)
        Game.playerArr.add(playerCard5)

        if (!Game.hasHadSplit)
        {
            playerCard1.text = savedInstanceState.getString("player1")
            playerCard2.text = savedInstanceState.getString("player2")
            playerCard3.text = savedInstanceState.getString("player3")
            playerCard4.text = savedInstanceState.getString("player4")
            playerCard5.text = savedInstanceState.getString("player5")
        }
        else
        {
            hand1.text = savedInstanceState.getString("splith1")
            hand2.text = savedInstanceState.getString("splith2")

            hand1card1.text = savedInstanceState.getString("splith1c1")
            hand1card2.text = savedInstanceState.getString("splith1c2")
            hand1card3.text = savedInstanceState.getString("splith1c3")
            hand1card4.text = savedInstanceState.getString("splith1c4")

            hand2card1.text = savedInstanceState.getString("splith2c1")
            hand2card2.text = savedInstanceState.getString("splith2c2")
            hand2card3.text = savedInstanceState.getString("splith2c3")
            hand2card4.text = savedInstanceState.getString("splith2c4")
        }


        dealerCard1.text = savedInstanceState.getString("dealer1")
        dealerCard2.text = savedInstanceState.getString("dealer2")
        dealerCard3.text = savedInstanceState.getString("dealer3")
        dealerCard4.text = savedInstanceState.getString("dealer4")
        dealerCard5.text = savedInstanceState.getString("dealer5")

    }



}