package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.waiting

import android.content.ContentResolver
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking.CarBikeBooking
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.tracking.TrackingDriver
import kotlinx.android.synthetic.main.activity_waiting.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.sdk27.coroutines.onClick

class WaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        val key = intent.getStringExtra("key")
        val from = intent.getStringExtra("from")
        Glide.with(this)
            .load(R.drawable.pin_eye2)
            .into(waiting)

//        toast(from)
//        val topic = intent.getStringExtra("topic")
        val ref = Config.databaseInstance(from)
//        pulsator.start()


        ref.child(key ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val book = p0.getValue(CarBikeBooking::class.java)
                    if (book?.status == 1) {
                        try {
                            val defaultSoundUri =
                                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.driver_found)
                            val r = RingtoneManager.getRingtone(this@WaitingActivity, defaultSoundUri)
                            r.play()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
//                        pulsator.stop()
                        finish()
                        startActivity(
                            intentFor<TrackingDriver>(
                                "booking" to book,
                                "from" to from
                            ).clearTop().clearTask().newTask()
                        )
                    }
                }

            })

        cancelBooking.onClick {
            ref.child(key ?: "").child("status").setValue(4)
//            pulsator.stop()
            finish()
        }

    }
}
