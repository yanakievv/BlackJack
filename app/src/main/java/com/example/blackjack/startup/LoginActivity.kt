package com.example.blackjack.startup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.blackjack.R
import com.example.blackjack.main.MainActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var callbackManager: CallbackManager
    val tag: String = "FacebookAuthentication"
    val noLoginButton: Boolean = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        profilePic.visibility = View.GONE

        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            greet.text = "Hello, " + Profile.getCurrentProfile().firstName + "."
            profilePic.visibility = View.VISIBLE
            profilePic.profileId = Profile.getCurrentProfile().id
        }


        loginButton.setOnClickListener {
            //Logout
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE
                ) {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()
                    greet.text = ""
                    profilePic.visibility = View.GONE
                }.executeAsync()
            }

            // Login
            loginButton.setPermissions("email", "public_profile")
            callbackManager = CallbackManager.Factory.create()
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
                                    greet.text = "Hello, " + currentProfile.firstName + "."
                                    profilePic.visibility = View.VISIBLE
                                    profilePic.profileId = currentProfile.id
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }




}




