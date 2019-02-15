package com.lauwba.surelabs.mishopcustomer.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.lauwba.surelabs.mishopcustomer.MainActivity
import com.lauwba.surelabs.mishopcustomer.R
import org.jetbrains.anko.startActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            finish()
            startActivity<MainActivity>()
        }, 3000)
    }
}
