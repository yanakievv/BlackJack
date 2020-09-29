package com.example.blackjack.startup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.blackjack.R
import com.example.blackjack.data.UserDAO
import com.example.blackjack.data.UserDatabase
import com.example.blackjack.main.MainActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val RC_SIGN_IN = 7


class LoginActivity : AppCompatActivity() {
    private lateinit var callbackManager: CallbackManager
    val tag: String = "FacebookAuthentication"
    private var db: UserDatabase? = null
    private var dbDAO: UserDAO? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        profilePic.visibility = View.GONE
        callbackManager = CallbackManager.Factory.create()
        db = UserDatabase.getInstance(this)
        dbDAO = db?.userDAO

        var accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        var account = GoogleSignIn.getLastSignedInAccount(this)

        if (accessToken != null && !accessToken.isExpired) {
            greet.text = "Hello, " + Profile.getCurrentProfile().firstName + " " + Profile.getCurrentProfile().lastName + "."
            CoroutineScope(Dispatchers.IO).launch {  walletText.text = "Wallet: " + dbDAO?.getWallet(Profile.getCurrentProfile().id as String).toString() + "$" }
            profilePic.visibility = View.VISIBLE
            profilePic.profileId = Profile.getCurrentProfile().id
            GloginButton.visibility = View.INVISIBLE
            FBloginButton.visibility = View.VISIBLE
            getCashButton.visibility = View.VISIBLE

        }
        else if (account != null) {
            greet.text = "Hello, " + account.displayName + "."
            CoroutineScope(Dispatchers.IO).launch { walletText.text = "Wallet: " + dbDAO?.getWallet(account!!.id as String).toString() + "$" }
            FBloginButton.visibility = View.INVISIBLE
            getCashButton.visibility = View.VISIBLE

            swapGButton(1)
        }
        else {
            FBloginButton.visibility = View.VISIBLE
            GloginButton.visibility = View.VISIBLE
            getCashButton.visibility = View.INVISIBLE
        }

        getCashButton.setOnClickListener {
            accessToken = AccessToken.getCurrentAccessToken()
            account = GoogleSignIn.getLastSignedInAccount(this)
            if (accessToken != null && !accessToken!!.isExpired) {
                displayFragment(accessToken!!.userId)
            }
            else if (account != null) {
                displayFragment(account!!.id!!)
            }
        }

        GloginButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            if (GoogleSignIn.getLastSignedInAccount(this) == null) { // Google Login
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            else { // Google Logout
                mGoogleSignInClient.signOut()
                swapGButton(0)
                greet.text = ""
                FBloginButton.visibility = View.VISIBLE
                walletText.text = ""
                getCashButton.visibility = View.INVISIBLE

            }
        }

        FBloginButton.setOnClickListener {
            // Facebook Logout
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE
                ) {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()
                    greet.text = ""
                    profilePic.visibility = View.GONE
                    GloginButton.visibility = View.VISIBLE
                    getCashButton.visibility = View.INVISIBLE
                    walletText.text = ""
                }.executeAsync()
            }

            // Facebook Login
            FBloginButton.setPermissions("email", "public_profile")
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    private lateinit var profileTracker: ProfileTracker

                    override fun onSuccess(loginResult: LoginResult?) {
                        if (Profile.getCurrentProfile() == null) {
                            profileTracker = object : ProfileTracker() {
                                @SuppressLint("SetTextI18n")
                                override fun onCurrentProfileChanged(
                                    oldProfile: Profile?,
                                    currentProfile: Profile
                                ) {
                                    Log.v("facebook - profile", currentProfile.firstName)
                                    profileTracker.stopTracking()
                                    greet.text =
                                        "Hello, " + currentProfile.firstName + " " + currentProfile.lastName + "."
                                    runBlocking {  walletText.text = "Wallet: " + dbDAO?.getWallet(currentProfile.id as String).toString() + "$" }
                                    profilePic.visibility = View.VISIBLE
                                    profilePic.profileId = currentProfile.id
                                    GloginButton.visibility = View.INVISIBLE
                                    getCashButton.visibility = View.VISIBLE

                                }
                            }

                        } else {
                            val profile = Profile.getCurrentProfile()
                            Log.v("facebook - profile", profile.firstName)
                        }
                    }

                    override fun onCancel() {
                        Log.d(tag, "Facebook onCancel.")
                    }

                    override fun onError(error: FacebookException) {
                        Log.d(tag, "Facebook onError.")
                    }
                }
            )
        }
        startButton.setOnClickListener{
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN && resultCode != 0) {
            if (GoogleSignIn.getLastSignedInAccount(this) != null) {
                greet.text = "Hello, " + GoogleSignIn.getLastSignedInAccount(this)?.displayName + "."
                FBloginButton.visibility = View.INVISIBLE
                getCashButton.visibility = View.VISIBLE
                swapGButton(1)
                CoroutineScope(Dispatchers.IO).launch {  walletText.text = "Wallet: " + dbDAO?.getWallet(GoogleSignIn.getLastSignedInAccount(applicationContext)?.id as String).toString() + "$" }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun swapGButton(id : Int) {
        val txtLogout = GloginButton.getChildAt(0) as TextView
        if (id == 1) {
            txtLogout.text = "Sign out"
        }
        else {
            txtLogout.text = "Sign in"
        }

    }

    @SuppressLint("SetTextI18n")

    private fun displayFragment(userId: String) {
        val getCashFragment = GetCashFragment.newInstance(userId)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(fragment_container.id, getCashFragment).commit()
    }


}




