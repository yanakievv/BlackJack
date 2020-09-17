package com.example.blackjack.contract

import android.content.Context
import com.example.blackjack.final.InputFromMain
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
        fun win(doubled: String)
        fun loss()
        fun tie()
        fun twentyOne()
        fun split()

        fun enableButtons()
        fun disableButtons()

    }

    interface FinalActivityPresenter: BasePresenter {

        fun connect(context: Context)
        suspend fun process(info: InputFromMain)


    }

    interface FinalView: BaseView<FinalActivityPresenter> {

    }
}