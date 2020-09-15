package com.example.blackjack.main

import com.example.blackjack.Contract

class MainActivityPresenter(var view: Contract.MainView?) : Contract.MainActivityPresenter {
    var game = Game()

    override fun init() {
        view?.disableButtons()

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

    override fun setUsername(userName: String) {
        game.userName = userName
    }

    override fun getUsername(): String {
        return game.userName
    }

    override fun hitAction() {
        view?.disableButtons()
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
                view?.bust()
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

    override fun dealerTurn() {
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

            if (game.dealerArr[0].value == 11 && game.dealerAce) {
                game.dealerSum -= 10
            }

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
                view?.split()
            }
            else {
                if ((game.dealerSum > 21 || game.playerSum > game.dealerSum) && game.playerSum <= 21) {
                    view?.win()
                }
                else if (game.dealerSum > game.playerSum || game.playerSum > 21) {
                    view?.loss()
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
        if (game.playerArr.size == 2 && game.playerArr[0].value == game.playerArr[1].value) {
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

        game.playerSum += game.playerArr[1].value
        game.playerSplitSum += game.playerSplitArr[1].value

        if (game.playerSum == 21) {
            swapHands()
        }

        view?.refreshView()
    }

    override fun doubleAction() {
        if (game.playerArr.size == 2 || (game.hasHadSplit && (game.playerArr.size == 2 || game.playerSplitArr.size == 2))) {
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

        view?.cleanUpView()
    }

}


