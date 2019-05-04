package com.lauwba.surelabs.mishopcustomer.tested

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lauwba.surelabs.mishopcustomer.R
import kotlinx.android.synthetic.main.activity_testing.*
import org.jetbrains.anko.toast

class TestingActivity : AppCompatActivity() {

    private var googleClient: GoogleSignInClient? = null
    private val signIn = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)

        logGoogle.setOnClickListener {
            val signInIntent = googleClient?.signInIntent
            startActivityForResult(signInIntent, signIn)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == signIn) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                toast(account.email.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
