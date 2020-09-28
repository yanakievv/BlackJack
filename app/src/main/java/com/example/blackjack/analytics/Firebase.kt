package com.example.blackjack.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics {
    private lateinit var fireBase: FirebaseAnalytics

    fun init(context: Context) {
        fireBase = FirebaseAnalytics.getInstance(context)
    }

    fun logMove(move: Int) {
        val bundle = Bundle()
        bundle.putInt("move_id", move) //0 for stand, 1 for hit, 2 for double, 3 for split
        fireBase.logEvent("moves", bundle)
    }
    fun logBet(bet: Int) {
        val bundle = Bundle()
        bundle.putInt("bet", bet)
        fireBase.logEvent("bets", bundle)
    }
    fun logOutcome(outcome: Int) {
        val bundle = Bundle()
        bundle.putInt("outcome", outcome) // -2 for two split hands lost | -1 for loss | 0 for tie(or one split hand won, one lost) | 1 for win | 2 for two split hands won
        fireBase.logEvent("outcomes", bundle)
    }
}