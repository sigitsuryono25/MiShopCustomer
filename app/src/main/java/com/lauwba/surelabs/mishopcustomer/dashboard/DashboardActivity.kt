package com.lauwba.surelabs.mishopcustomer.dashboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.toolbar.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)


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
                R.id.orderan -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.container,
                            ProsesFragment()
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
//                R.id.bantuan -> {
//                    return@setOnNavigationItemSelectedListener true
//                }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.bantuan -> {
//                startActivity<TentangActivity>()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
