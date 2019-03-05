package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.waiting

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking.CarBikeBooking
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.tracking.TrackingDriver
import kotlinx.android.synthetic.main.activity_waiting.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class WaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        val key = intent.getStringExtra("key")
//        val topic = intent.getStringExtra("topic")
        val ref = Config.databaseInstance(Constant.TB_CAR_ORDER)
        pulsator.start()


        ref.child(key ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val book = p0.getValue(CarBikeBooking::class.java)
                    if (book?.status == 2) {
                        toast("nyoh drivermu")
                        pulsator.stop()

                        startActivity<TrackingDriver>("booking" to book)
                    }
                }

            })

        cancelBooking.onClick {
            ref.child(key ?: "").child("status").setValue(3)
            pulsator.stop()
            finish()
        }

    }
}
