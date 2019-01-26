package com.lauwba.surelabs.mishopcustomer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide

class LoginActivity : AppCompatActivity() {

    lateinit var anim: ImageView
    lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        anim = findViewById(R.id.anim)
        login = findViewById(R.id.login)

        Glide.with(applicationContext)
            .load("https://i.gifer.com/Tvth.gif")
            .into(anim)

        login.setOnClickListener {
            finish()
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
    }
}
