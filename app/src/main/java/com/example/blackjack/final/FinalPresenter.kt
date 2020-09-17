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

    private fun getUID(username: String): Int {
        var uID: Int
        runBlocking {
            if (dbDAO?.checkUser(username) == 0)
            {
                dbDAO?.addUser(User(username = username))
            }
            uID = dbDAO?.getUserId(username) as Int
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
        val uID: Int = getUID(info.username as String)

        bestCombo = dbDAO?.getBestStreak(uID) as Int
        combo = dbDAO?.getStreak(uID) as Int

        if (info.split == "f") {
            if (info.outcome == "Bust!" || info.outcome == "Dealer won.") {
                dbDAO?.incLoss(uID)
                alterStreak(false, uID)
            }
            else if (info.outcome != "Tied.") {
                dbDAO?.incWin(uID)
                alterStreak(true, uID)
                if (info.double == "t")
                {
                    dbDAO?.incDoubleWon(uID)
                }
            }
        }
        else
        {
            if (compareHands(Integer.valueOf(info.player as String), Integer.valueOf(info.outcome as String)))
            {
                dbDAO?.incSplitWin(uID)
                alterStreak(true, uID)
            }
            else if (Integer.valueOf(info.player as String) != Integer.valueOf(info.outcome as String))
            {
                dbDAO?.incSplitLoss(uID)
                alterStreak(false, uID)
            }
            if (compareHands(Integer.valueOf(info.dealer as String), Integer.valueOf(info.outcome as String)))
            {
                dbDAO?.incSplitWin(uID)
                alterStreak(true, uID)
            }
            else if (Integer.valueOf(info.dealer as String) != Integer.valueOf(info.outcome as String))
            {
                dbDAO?.incSplitLoss(uID)
                alterStreak(false, uID)
            }
        }
    }

    override fun onDestroy() {
        this.view = null
    }
}