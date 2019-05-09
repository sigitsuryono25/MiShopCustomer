package com.lauwba.surelabs.mishopcustomer.appintro

import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.os.Bundle
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.MaterialIntroActivity
import com.lauwba.surelabs.mishopcustomer.dashboard.DashboardActivity
import com.pixplicity.easyprefs.library.Prefs
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class AppIntroActivity : MaterialIntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Prefs.getBoolean(Constant.INTRO, true)) {
            startActivity(intentFor<DashboardActivity>().clearTop().clearTask().newTask())
            return
        }

        addSlide(
            SlideFragmentBuilder()
                .title("Mi Shop")
                .description(
                    "Sebuah layanan yang berisi penawaran yang terdiri dari makanan dan non makanan. " +
                            "Anda dapat langsung menghubungi mitra yang sedang menawarkan jasanya"
                )
                .backgroundColor(android.R.color.white)
                .buttonsColor(R.color.blue_gj)
                .image(R.drawable.new_shop)
                .neededPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                .build()
        )

        addSlide(
            SlideFragmentBuilder()
                .title("Mi Service")
                .description(
                    "Sebuah layanan yang berisi penawaran Jasa. " +
                            "Anda dapat langsung menghubungi mitra yang sedang menawarkan jasanya " +
                            "baik via Whatsapp, SMS, ataupun Telepon. Anda dapat langsung melakukan booking " +
                            "bila telah mencapai kesepakatan"
                )
                .backgroundColor(android.R.color.white)
                .image(R.drawable.new_services)
                .buttonsColor(R.color.blue_gj)
                .neededPermissions(
                    arrayOf(
                        android.Manifest.permission.CALL_PHONE,
                        android.Manifest.permission.READ_CONTACTS
                    )
                )
                .build()
        )

        addSlide(
            SlideFragmentBuilder()
                .title("Mi Car")
                .description("Sebuah layanan Antar Jemput dengan menggunakan Mobil.")
                .image(R.drawable.ic_group_331)
                .backgroundColor(android.R.color.white)
                .buttonsColor(R.color.blue_gj)
                .neededPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                .build()
        )

        addSlide(
            SlideFragmentBuilder()
                .title("Mi Express")
                .description("Sebuah layanan Antar Jemput Barang dengan Harga Bersaing. ")
                .buttonsColor(R.color.blue_gj)
                .backgroundColor(android.R.color.white)
                .image(R.drawable.new_express)
                .neededPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                .build()
        )

        addSlide(
            SlideFragmentBuilder()
                .title("Mi Bike")
                .description("Sebuah layanan Antar Jemput dengan menggunakan Motor.")
                .image(R.drawable.new_bike)
                .backgroundColor(android.R.color.white)
                .buttonsColor(R.color.blue_gj)
                .neededPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                .build()
        )
    }

    override fun sendData() {
        super.sendData()
        Prefs.putBoolean(Constant.INTRO, false)
        finish()
        startActivity(intentFor<DashboardActivity>().clearTask().clearTop().newTask())
    }

}
