package com.lauwba.surelabs.mishopcustomer.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.config.Constant

class NotificationServices : Service() {
    private val ref = Constant.database.reference
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mishopListener()
        serviceListener()
        carListener()
        bikeListener()
        expressListener()
        return START_STICKY
    }

    private fun mishopListener() {
        ref.child(Constant.TB_SHOP)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun serviceListener() {
        ref.child(Constant.TB_SERVICE)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun carListener() {
        ref.child(Constant.TB_CAR)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun bikeListener() {
        ref.child(Constant.TB_BIKE)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun expressListener() {
        ref.child(Constant.TB_EXPRESS)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun setToView() {

    }


}