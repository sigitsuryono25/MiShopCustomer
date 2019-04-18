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
                .backgroundColor(R.color.mishop)
                .buttonsColor(R.color.micar)
                .image(R.mipmap.ic_shop)
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
                .backgroundColor(R.color.miservice)
                .image(R.mipmap.ic_service)
                .buttonsColor(android.R.color.holo_red_dark)
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
                .backgroundColor(R.color.micar)
                .image(R.mipmap.ic_car)
                .buttonsColor(R.color.miservice)
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
                .backgroundColor(R.color.miexpress)
                .buttonsColor(R.color.mishop)
                .image(R.mipmap.ic_express)
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
                .backgroundColor(R.color.mibike)
                .image(R.mipmap.ic_bike)
                .buttonsColor(R.color.miexpress)
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
