package com.lauwba.surelabs.mishopcustomer.splash.ui


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.login.LoginActivity
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.pixplicity.easyprefs.library.Prefs
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity

class SplashScreenSecond : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Prefs.contains(Constant.UID)) {
            checkSuspended(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
        } else {
            Handler().postDelayed({
                activity?.finish()
                startActivity<LoginActivity>()
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
                                        activity?.finish()
                                        startActivity<LoginActivity>()
                                    }, 3000)
                                } else {
                                    alert {
                                        message = "Mohon Maaf kak\nAkun kakak sementara ini " +
                                                "terkena suspend hingga ${HourToMillis.millisToDateHour(suspend)}. " +
                                                "\nTerima Kasih"
                                        okButton { activity?.finish() }
                                        isCancelable = false
                                    }.show()
                                }
                            } else {
                                Handler().postDelayed({
                                    activity?.finish()
                                    startActivity<LoginActivity>()
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
