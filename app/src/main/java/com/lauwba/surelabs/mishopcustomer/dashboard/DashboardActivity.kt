package com.lauwba.surelabs.mishopcustomer.dashboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.HomeFragment
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.InboxFragment
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.NotifikasiFragment
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.ProfileFragment
import kotlinx.android.synthetic.main.bottom_nav.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)



        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.container,
                            HomeFragment()
                        ).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.inbox -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.container,
                            InboxFragment()
                        ).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bantuan -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.notifikasi -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, NotifikasiFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.akun -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, ProfileFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
        try {
            val notif = intent.getIntExtra("notif", 0)
            if (notif == 1024) {
                navigation.selectedItemId = R.id.notifikasi
                supportFragmentManager.beginTransaction().replace(
                    R.id.container,
                    NotifikasiFragment()
                ).commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.container,
                        HomeFragment()
                    ).commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
