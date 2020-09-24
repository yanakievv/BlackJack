package com.example.blackjack.final

import android.content.Context
import com.example.blackjack.contract.Contract
import com.example.blackjack.data.User
import com.example.blackjack.data.UserDAO
import com.example.blackjack.data.UserDatabase
import kotlinx.coroutines.runBlocking

class FinalActivityPresenter(var view: Contract.FinalView?) : Contract.FinalActivityPresenter {
    private var db: UserDatabase? = null
    private var dbDAO : UserDAO? = null

    var bestCombo: Int = 0
    var combo: Int = 0

    private suspend fun alterStreak(hasWon: Boolean, uID: Int)
    {
        if (hasWon) combo++ else combo = 0

        if (combo > bestCombo)
        {
            bestCombo = combo
            dbDAO?.setBestStreak(uID, combo)
        }

        dbDAO?.setStreak(uID, combo)

    }

    private fun getUID(fbID: String, username: String): Int {
        var uID: Int
        runBlocking {
            if (dbDAO?.checkUser(fbID) == 0)
            {
                dbDAO?.addUser(User(fbID = fbID, username = username))
            }
            uID = dbDAO?.getUserId(fbID) as Int
        }

        return uID
    }

    private fun compareHands(player: Int, dealer: Int): Boolean {
        return (player in (dealer + 1)..21) || (dealer > 21 && player <= 21)
    }

    override fun connect(context: Context) {
        db = UserDatabase.getInstance(context)
        dbDAO = db?.userDAO
    }

    override suspend fun process(info: InputFromMain) {
        val uID: Int = getUID(info.fbID as String, info.username as String)
        val overallID: Int = getUID("Overall","Overall")

        if (dbDAO?.getUsername(uID) != info.username) {
            dbDAO?.updateUser(uID, info.username as String)
        }

        bestCombo = dbDAO?.getBestStreak(uID) as Int
        combo = dbDAO?.getStreak(uID) as Int

        if (info.split == "f") {
            if (info.outcome == "Bust!" || info.outcome == "Dealer won.") {
                dbDAO?.incLoss(uID)
                dbDAO?.incLoss(overallID)
                alterStreak(false, uID)
            }
            else if (info.outcome != "Tied.") {
                dbDAO?.incWin(uID)
                dbDAO?.incWin(overallID)
                alterStreak(true, uID)
                if (info.double == "t")
                {
                    dbDAO?.incDoubleWon(uID)
                    dbDAO?.incDoubleWon(overallID)
                }
            }
        }
        else
        {
            if (compareHands(Integer.valueOf(info.player as String), Integer.valueOf(info.outcome as String)))
            {
                dbDAO?.incSplitWin(uID)
                dbDAO?.incSplitWin(overallID)
                alterStreak(true, uID)
            }
            else if (Integer.valueOf(info.player as String) != Integer.valueOf(info.outcome as String) || Integer.valueOf(info.player as String) > 21)
            {
                dbDAO?.incSplitLoss(uID)
                dbDAO?.incSplitLoss(overallID)
                alterStreak(false, uID)
            }
            if (compareHands(Integer.valueOf(info.dealer as String), Integer.valueOf(info.outcome as String)))
            {
                dbDAO?.incSplitWin(uID)
                dbDAO?.incSplitWin(overallID)
                alterStreak(true, uID)
            }
            else if (Integer.valueOf(info.dealer as String) != Integer.valueOf(info.outcome as String) || Integer.valueOf(info.dealer as String) > 21)
            {
                dbDAO?.incSplitLoss(uID)
                dbDAO?.incSplitLoss(overallID)
                alterStreak(false, uID)
            }
        }
    }

    override fun onDestroy() {
        this.view = null
    }
}