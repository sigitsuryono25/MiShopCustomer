package com.lauwba.surelabs.mishopcustomer

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    lateinit var fm: FragmentManager
    lateinit var ft: FragmentTransaction
    lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bottomNavigationView = findViewById(R.id.navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    fm = supportFragmentManager
                    ft = fm.beginTransaction()
                    ft.replace(R.id.container, HomeFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.inbox -> {
                    fm = supportFragmentManager
                    ft = fm.beginTransaction()
                    ft.replace(R.id.container, InboxFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bantuan -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.notifikasi -> {
                    fm = supportFragmentManager
                    ft = fm.beginTransaction()
                    ft.replace(R.id.container, NotifikasiFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.akun -> {
                    fm = supportFragmentManager
                    ft = fm.beginTransaction()
                    ft.replace(R.id.container, ProfileFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        fm = supportFragmentManager
        ft = fm.beginTransaction()
        ft.replace(R.id.container, HomeFragment()).commit()
    }


}
