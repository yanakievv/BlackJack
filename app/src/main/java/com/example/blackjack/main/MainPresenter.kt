package com.example.blackjack.main

import android.content.Context
import com.example.blackjack.analytics.Analytics
import com.example.blackjack.contract.Contract
import com.example.blackjack.data.UserDAO
import com.example.blackjack.data.UserDatabase
import kotlinx.coroutines.runBlocking

class MainActivityPresenter(var view: Contract.MainView?) : Contract.MainActivityPresenter {
    private var game = Game()
    private val firebase = Analytics()
    private var db: UserDatabase? = null
    private var dbDAO : UserDAO? = null

    override fun init(context: Context) {
        view?.disableButtons()
        firebase.init(context)
        for (decks in 1..game.deck.numOfDecks) {
            for (i in 2..10) {
                game.deck.cards.add(Card(i, CardSuit.CLUBS, i.toString()))
                game.deck.cards.add(Card(i, CardSuit.SPADE, i.toString()))
                game.deck.cards.add(Card(i, CardSuit.DIAMOND, i.toString()))
                game.deck.cards.add(Card(i, CardSuit.HEART, i.toString()))
            }
            // Aces
            game.deck.cards.add(Card(11, CardSuit.CLUBS, "A"))
            game.deck.cards.add(Card(11, CardSuit.SPADE, "A"))
            game.deck.cards.add(Card(11, CardSuit.DIAMOND, "A"))
            game.deck.cards.add(Card(11, CardSuit.HEART, "A"))
            // Kings
            game.deck.cards.add(Card(10, CardSuit.CLUBS, "K"))
            game.deck.cards.add(Card(10, CardSuit.SPADE, "K"))
            game.deck.cards.add(Card(10, CardSuit.DIAMOND, "K"))
            game.deck.cards.add(Card(10, CardSuit.HEART, "K"))
            // Queens
            game.deck.cards.add(Card(10, CardSuit.CLUBS, "Q"))
            game.deck.cards.add(Card(10, CardSuit.SPADE, "Q"))
            game.deck.cards.add(Card(10, CardSuit.DIAMOND, "Q"))
            game.deck.cards.add(Card(10, CardSuit.HEART, "Q"))
            // Jacks
            game.deck.cards.add(Card(10, CardSuit.CLUBS, "J"))
            game.deck.cards.add(Card(10, CardSuit.SPADE, "J"))
            game.deck.cards.add(Card(10, CardSuit.DIAMOND, "J"))
            game.deck.cards.add(Card(10, CardSuit.HEART, "J"))

            game.deck.cards.shuffle()
        }

        initGame()
    }

    override fun initGame() {
        game.dealerHadTurn = false
        game.it = game.deck.cards.iterator()

        game.playerSum = 0
        game.playerSplitSum = 0
        game.dealerSum = 0

        game.hasHadSplit = false
        game.hasSplit = false

        game.playerArr.clear()
        game.dealerArr.clear()
        game.playerSplitArr.clear()

        game.playerAce = false
        game.dealerAce = false

        game.dealerArr.add(Card(0, CardSuit.NONE, "H"))
        game.playerArr.add(game.it.next())

        game.dealerArr.add(game.it.next())
        game.playerArr.add(game.it.next())

        if (game.dealerArr[1].value == 11) {
            game.dealerAce = true
        }

        if (game.playerArr[0].value == 11 || game.playerArr[1].value == 11) {
            game.playerAce = true
        }

        game.dealerSum += game.dealerArr[1].value
        game.playerSum += game.playerArr[0].value
        game.playerSum += game.playerArr[1].value

        view?.init()

        if (game.playerSum == 21) {
            view?.twentyOne()
        }
        else {
            view?.enableButtons()
        }

    }

    override fun getDealerHand(): MutableList<Card> {
        return game.dealerArr
    }

    override fun getPlayerHand(): MutableList<Card> {
        return game.playerArr
    }

    override fun getPlayerSplitHand(): MutableList<Card> {
        return game.playerSplitArr
    }

    override fun getPlayerSum(): Int {
        return game.playerSum
    }

    override fun getPlayerSplitSum(): Int {
        return game.playerSplitSum
    }

    override fun getDealerSum(): Int {
        return game.dealerSum
    }

    override fun connect(context: Context) {
        db = UserDatabase.getInstance(context)
        dbDAO = db?.userDAO
    }

    override fun setBet(userID: String, bet: Int, context: Context): Boolean {
        firebase.init(context)
        val wallet: Int
        runBlocking {
            wallet = dbDAO!!.getWallet(userID) }
        return if (wallet < bet) {
            false
        }
        else {
            game.wallet = wallet - bet
            firebase.logBet(bet)
            game.bet = bet
            true
        }
    }

    override fun getBet(): Int {
        return game.bet
    }

    override fun hitAction() {
        view?.disableButtons()
        firebase.logMove(1)
        val newCard = game.it.next()

        if (newCard.value == 11) {
            game.playerAce = true
        }

        game.playerArr.add(newCard)

        game.playerSum += newCard.value

        if (game.playerArr.size == 6) {
            dealerTurn()
        }

        if (game.playerSum > 21 && !game.playerAce) {
            if (game.hasSplit) {
                swapHands()
            }
            else if (game.hasHadSplit && game.playerSplitSum <= 21) {
                dealerTurn()
            }
            else {
                view?.bust(if (game.hasDoubled) "t" else "f")
            }
        }
        else if (game.playerSum > 21 && game.playerAce) {
            game.playerSum -= 10
            game.playerAce = false

        }
        else if (game.playerSum == 21) {
            if (game.hasSplit) {
                swapHands()
            }
            else {
                dealerTurn()
            }
        }

        view?.refreshView()
        view?.enableButtons()
    }

    override fun standAction() {
        firebase.logMove(0)
        dealerTurn()
    }

    private fun dealerTurn() {
        view?.disableButtons()

        if (!game.hasHadSplit && game.playerSum > 21) {
            return
        }
        if (!game.hasSplit) {
            if (game.dealerHadTurn) {
                return
            }
            else {
                game.dealerHadTurn = true
            }
            game.dealerArr[0] = game.it.next()
            if (game.dealerArr[0].value == 11) {
                game.dealerAce = true
            }
            game.dealerSum += game.dealerArr[0].value

            while (game.dealerSum < 17) {

                if (game.dealerArr.size == 5){
                    break
                }

                val tempCard = game.it.next()
                game.dealerArr.add(tempCard)

                if (tempCard.value == 11) {
                    game.dealerAce = true
                }

                if (game.dealerSum + tempCard.value > 21 && game.dealerAce) {
                    game.dealerSum -= 10
                    game.dealerAce = false
                }

                game.dealerSum += tempCard.value
                view?.refreshView()
            }

            view?.refreshView()

            if (game.hasHadSplit) {
                view?.split(if (game.hasDoubled) "t" else "f", if (game.hasSplitDoubled) "t" else "f")
            }
            else {
                if ((game.dealerSum > 21 || game.playerSum > game.dealerSum) && game.playerSum <= 21) {
                    view?.win(if (game.hasDoubled) "t" else "f")
                }
                else if (game.dealerSum > game.playerSum || game.playerSum > 21) {
                    view?.loss(if (game.hasDoubled) "t" else "f")
                }
                else {
                    view?.tie()
                }
            }
        }
        else {
            view?.enableButtons()
            swapHands()
        }
    }

    override fun splitAction() {
        if (game.wallet >= game.bet && game.playerArr.size == 2 && game.playerArr[0].value == game.playerArr[1].value && !game.hasHadSplit) {
            firebase.logMove(3)
            initSplit()
        }
    }

    private fun initSplit() {
        game.playerSplitArr.add(game.playerArr[1])
        game.playerArr[1] = Card(0, CardSuit.NONE, "")
        view?.refreshView()
        game.playerArr.removeAt(1)
        game.hasSplit = true
        game.hasHadSplit = true
        game.playerSum /= 2
        game.playerSplitSum = game.playerSum

        game.playerArr.add(game.it.next())
        game.playerSplitArr.add(game.it.next())

        if (game.playerArr[1].value == 11) {
            game.playerAce = true
        }

        game.playerSum += game.playerArr[1].value
        game.playerSplitSum += game.playerSplitArr[1].value

        if (game.playerSum == 21) {
            swapHands()
        }

        view?.refreshView()

        if (game.playerArr[0].value == 11)
        {
            hitAction()
            swapHands()
            hitAction()
        }

    }

    override fun doubleAction() {
        if (game.wallet >= game.bet && game.playerArr.size == 2 || (game.hasHadSplit && game.playerSplitArr.size == 2)) {
            firebase.logMove(2)
            if (!game.hasHadSplit || (game.hasSplit && game.hasHadSplit)) {
                game.hasDoubled = true
            }
            if (!game.hasSplit && game.hasHadSplit) {
                game.hasSplitDoubled = true
            }
            hitAction()
            dealerTurn()
        }
    }

    override fun onDestroy() {
        this.view = null
    }

    private fun swapHands() {
        game.hasSplit = false
        val temp = game.playerArr
        game.playerArr = game.playerSplitArr
        game.playerSplitArr = temp


        game.playerSum = game.playerSum + game.playerSplitSum
        game.playerSplitSum = game.playerSum - game.playerSplitSum
        game.playerSum = game.playerSum - game.playerSplitSum

        if (game.playerArr[1].value == 11) {
            game.playerAce = true
        }

        view?.cleanUpView()

        if (game.playerSum == 21)
        {
            view?.split(if (game.hasDoubled) "t" else "f", if (game.hasSplitDoubled) "t" else "f")
        }
    }
}


