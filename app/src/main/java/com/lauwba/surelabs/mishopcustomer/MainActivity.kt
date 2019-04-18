package com.lauwba.surelabs.mishopcustomer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.lauwba.surelabs.mishopcustomer.appintro.AppIntroActivity
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.login.LoginActivity
import com.lauwba.surelabs.mishopcustomer.registrasi.NewRegistrasi
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        if (Prefs.contains(Constant.INTRO)) {
            startActivity(intentFor<AppIntroActivity>().clearTop().newTask())
        }

        Glide.with(applicationContext)
            .load("https://i.gifer.com/Tvth.gif")
            .into(anim)

        login.onClick {
            startActivity(intentFor<LoginActivity>().newTask().clearTop())
        }

        register.onClick {
            startActivity(intentFor<NewRegistrasi>().newTask().clearTop())

        }

    }
}
