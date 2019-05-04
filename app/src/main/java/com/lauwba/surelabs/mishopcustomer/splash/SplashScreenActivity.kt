package com.lauwba.surelabs.mishopcustomer.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MainActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.pixplicity.easyprefs.library.Prefs
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.new_activity_splash_screen)

        if (Prefs.contains(Constant.UID)) {
            checkSuspended(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
        } else {
            Handler().postDelayed({
                finish()
                startActivity<MainActivity>()
            }, 3000)
        }
    }

    private fun checkSuspended(uid: String?) {
        val ref = Constant.database.getReference(Constant.TB_CUSTOMER)
        ref.orderByChild("uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issues in p0.children) {
                            val data = issues.getValue(Customer::class.java)
                            val suspend = data?.masaSuspend
                            if (suspend?.equals(0) == false) {
                                if (HourToMillis.millis() > suspend) {
                                    ref.child(issues.key.toString())
                                        .child("masaSuspend")
                                        .setValue(0)
                                    ref.child(issues.key.toString())
                                        .child("statusAktif")
                                        .setValue(0)

                                    Handler().postDelayed({
                                        finish()
                                        startActivity<MainActivity>()
                                    }, 3000)
                                } else {
                                    alert {
                                        message = "Mohon Maaf kak\nAkun kakak sementara ini " +
                                                "terkena suspend hingga ${HourToMillis.millisToDateHour(suspend)}. " +
                                                "\nTerima Kasih"
                                        okButton { finish() }
                                        isCancelable = false
                                    }.show()
                                }
                            } else {
                                Handler().postDelayed({
                                    finish()
                                    startActivity<MainActivity>()
                                }, 3000)
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

}
