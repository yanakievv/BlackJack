package com.example.blackjack

import com.example.blackjack.main.BasePresenter
import com.example.blackjack.main.BaseView
import com.example.blackjack.main.Card

interface Contract {

    interface MainActivityPresenter: BasePresenter {

        fun init()
        fun initGame()

        fun getDealerHand(): MutableList<Card>
        fun getPlayerHand(): MutableList<Card>
        fun getPlayerSplitHand(): MutableList<Card>

        fun getPlayerSum(): Int
        fun getPlayerSplitSum(): Int
        fun getDealerSum(): Int

        fun setUsername(userName: String)
        fun getUsername(): String

        fun hitAction()
        fun dealerTurn()
        fun splitAction()
        fun doubleAction()
    }

    interface MainView: BaseView<MainActivityPresenter> {

        fun init()
        fun refreshView()
        fun cleanUpView()

        fun bust()
        fun win()
        fun loss()
        fun tie()
        fun twentyOne()
        fun split()

        fun enableButtons()
        fun disableButtons()

    }
}