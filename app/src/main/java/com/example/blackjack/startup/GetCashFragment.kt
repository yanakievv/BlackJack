package com.example.blackjack.startup

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.blackjack.R
import com.example.blackjack.data.UserDAO
import com.example.blackjack.data.UserDatabase
import kotlinx.android.synthetic.main.fragment_get_cash.view.*
import kotlinx.coroutines.runBlocking


@Suppress("NAME_SHADOWING", "DEPRECATION")
class GetCashFragment : Fragment() {

    private lateinit var userId: String
    private var db: UserDatabase? = null
    private var dbDAO: UserDAO? = null

    @SuppressLint("SetTextI18n")
    private fun makeTransaction(money: Int) {
        var temp: String
        runBlocking {
            dbDAO!!.updateWallet(userId, money)
            temp = "Wallet: " + dbDAO?.getWallet(userId).toString() + "$"
        }
        val wallet = requireActivity().findViewById<View>(R.id.walletText) as TextView
        wallet.text = temp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("userId") as String
            db = activity?.let { it -> UserDatabase.getInstance(it) }
            dbDAO = db?.userDAO
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_get_cash, container, false)
        view.backButton.setOnClickListener {
            fragmentManager?.beginTransaction()!!.remove(this).commit()
        }
        view.getCash10.setOnClickListener {
            makeTransaction(10)
        }
        view.getCash50.setOnClickListener {
            makeTransaction(50)
        }
        view.getCash100.setOnClickListener {
            makeTransaction(100)
        }
        view.getCash500.setOnClickListener {
            makeTransaction(500)
        }
        view.getCash1000.setOnClickListener {
            makeTransaction(1000)
        }
        view.getCash2000.setOnClickListener {
            makeTransaction(2000)
        }
        return view
    }

    companion object {

        fun newInstance(userId: String,) =
            GetCashFragment().apply {
                arguments = Bundle().apply {
                    putString("userId", userId)

                }
            }
    }

}