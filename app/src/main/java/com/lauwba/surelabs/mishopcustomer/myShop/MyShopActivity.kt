package com.lauwba.surelabs.mishopcustomer.myShop

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.myShop.ui.myshopdashoard.BrowseMyShop
import com.lauwba.surelabs.mishopcustomer.myShop.ui.myshopdashoard.MyShopDashoard
import kotlinx.android.synthetic.main.my_shop_activity.*
import org.jetbrains.anko.alert

class MyShopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_shop_activity)

        supportFragmentManager.beginTransaction().replace(R.id.container, MyShopDashoard())
            .commit()

        checkPermission()

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, MyShopDashoard())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.browseShop -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, BrowseMyShop())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }


    private fun checkPermission() {
//        if (!PreferenceManager.getDefaultSharedPreferences(this@MiShopActivity).getBoolean("mishop", false)) {
//            alert("Layanan Mi Shop Belum Diaktikan, Silahkan Aktifkan Terlebih Dahulu", "Peringatan") {
//                yesButton { startActivity<LayananActivity>() }
//                noButton {
//                    finish()
//                }
//            }.show()
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), 1
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {

        }
    }

    override fun onBackPressed() {
        if (realpath.isNullOrEmpty()) {
            finish()
        } else {
            alert("Ingin Kembali ke dashboard?") {
                positiveButton("Ya") {
                    finish()
                }
                negativeButton("Tidak") {

                }
            }.show()
        }
    }


    companion object {
        var realpath: String? = null
    }

}
