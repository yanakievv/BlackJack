package com.example.blackjack.startup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.blackjack.R
import com.example.blackjack.main.MainActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var callbackManager: CallbackManager
    val tag: String = "FacebookAuthentication"
    val noLoginButton: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            startActivity(Intent(applicationContext, PlayActivity::class.java))
        }


        loginButton.setOnClickListener {
            // Login
            callbackManager = CallbackManager.Factory.create()
            loginButton.setPermissions("email", "public_profile")
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d(tag, "Facebook token: " + loginResult.accessToken.token)
                        startActivity(Intent(applicationContext, PlayActivity::class.java))
                    }

                    override fun onCancel() {
                        Log.d(tag, "Facebook onCancel.")
                    }

                    override fun onError(error: FacebookException) {
                        Log.d(tag, "Facebook onError.")
                    }
                })
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


