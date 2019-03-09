package com.lauwba.surelabs.mishopcustomer.dashboard

import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.*
import com.lauwba.surelabs.mishopcustomer.kritik.KritikSaranActivity
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

        switchOn.visibility = View.VISIBLE
        switchOnCheck()
        setBadges()

        if (!Prefs.getBoolean(Constant.SERVICE, false)) {
            switchOn.isChecked = false
            toast("Layanan Belum Diaktikan")
        } else {
            switchOn.isChecked = true
        }


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
                R.id.notifikasi -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, NewNotificationFragment()).commit()
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

    private fun setBadges() {
        val bottomNavigationMenuView = navigation.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(2)
        val itemView = v as BottomNavigationItemView

        val badge = LayoutInflater.from(this)
            .inflate(R.layout.badges, itemView, true)
        val count = badge.findViewById<TextView>(R.id.notifications)
    }

    private fun switchOnCheck() {
        switchOn.onCheckedChange { buttonView, isChecked ->
            if (isChecked) {
                try {
                    FirebaseMessaging.getInstance().subscribeToTopic("mishopcustomer")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("MISHOP", "successful subcsribe")
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    FirebaseMessaging.getInstance().subscribeToTopic("miservicecustomer")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("MISERVICE", "successful subcsribe")
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    FirebaseMessaging.getInstance().subscribeToTopic("micarcustomer")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("MICAR", "successful subcsribe")
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    FirebaseMessaging.getInstance().subscribeToTopic("mibikecustomer")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("MIBIKE", "successful subcsribe")
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
//                    sharedPreferences = getPreferences(Context.MODE_PRIVATE)
//                    sharedPreferences?.edit()?.putBoolean(Constant.SERVICE, true)?.apply()
                    Prefs.putBoolean(Constant.SERVICE, true)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("mishopcustomer")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MISHOP", "successful unsubscribeFromTopic")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                FirebaseMessaging.getInstance().unsubscribeFromTopic("miservicecustomer")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MISERVICE", "successful unsubscribeFromTopic")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                FirebaseMessaging.getInstance().unsubscribeFromTopic("micarcustomer")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MICAR", "successful unsubscribeFromTopic")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                FirebaseMessaging.getInstance().unsubscribeFromTopic("mibikecustomer")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MIBIKE", "successful unsubscribeFromTopic")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                Prefs.putBoolean(Constant.SERVICE, false)

            }
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
            R.id.kritik -> {
                startActivity<KritikSaranActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
