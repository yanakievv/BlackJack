package com.example.blackjack.main

enum class CardSuit(val type: Char) {
    CLUBS('\u2663'),
    SPADE('\u2660'),
    DIAMOND('\u2666'),
    HEART('\u2665'),
    NONE(' ')
}

class Card(val value: Int, val suit: CardSuit, val ref: String)

class Deck(val numOfDecks: Int = 1) {
    var cards = mutableListOf<Card>()
}

class Game {
    var deck: Deck = Deck(1)

    var playerSum: Int = 0
    var playerSplitSum: Int = 0
    var dealerSum: Int = 0

    var it = deck.cards.iterator()
    var dealerArr = mutableListOf<Card>()
    var playerArr = mutableListOf<Card>()
    var playerSplitArr = mutableListOf<Card>()

    var playerAce: Boolean = false
    var dealerAce: Boolean = false

    var hasSplit: Boolean = false
    var hasHadSplit: Boolean = false
    var hasDoubled: Boolean = false

    var userName: String = "Player"

    var dealerHadTurn: Boolean = false
}
