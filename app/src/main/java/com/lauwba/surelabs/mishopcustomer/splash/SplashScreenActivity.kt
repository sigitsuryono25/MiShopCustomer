package com.lauwba.surelabs.mishopcustomer.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.splash.ui.SplashScreenFirst
import com.lauwba.surelabs.mishopcustomer.splash.ui.SplashScreenSecond


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toggleHideyBar()
        setContentView(R.layout.new_activity_splash_screen)

        supportFragmentManager.beginTransaction().replace(R.id.container, SplashScreenFirst())
            .commit()

        Handler().postDelayed({
            supportFragmentManager.beginTransaction().replace(R.id.container, SplashScreenSecond())
                .commit()
        }, 3000)
    }

    fun toggleHideyBar() {
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}
