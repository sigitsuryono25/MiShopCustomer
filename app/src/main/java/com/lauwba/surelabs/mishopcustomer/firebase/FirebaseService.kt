package com.lauwba.surelabs.mishopcustomer.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model.Distance
import com.lauwba.surelabs.mishopcustomer.chat.model.ItemChat
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.notification.NotificationHandler
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.lauwba.surelabs.mishopcustomer.sqlite.InsertQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class FirebaseService : FirebaseMessagingService() {
    private var insert: InsertQuery? = null
    private var dis: CompositeDisposable? = null
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.i("TOKEN", p0)
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        if (p0?.data?.isNotEmpty() != false) {
            val data = p0?.data
            Log.i("DATA", p0?.data?.toString())

            try {

//                if (!Prefs.getBoolean(Constant.SERVICE, false)) {
//
//                } else {
                if (data.toString().contains("message", true)) {
                    val message = JSONObject(data?.get("data"))
                    setToView(message)
                } else {
                    val obj = JSONObject(data)
                    val lat = obj.getDouble("lat")
                    val lon = obj.getDouble("lon")
                    val title = obj.getString("title")
                    val desc = obj.getString("deskripsi")
                    checkRealtLocation(Constant.mAuth.currentUser?.uid, lat, lon, title, desc)

//                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (p0?.notification != null) {
            Log.d("onMessageReceived", p0.notification?.body)
        }
    }

    private fun checkRealtLocation(uid: String?, lat: Double, lon: Double, title: String?, desc: String?) {
        val ref = Constant.database.getReference(Constant.TB_CUSTOMER)
        ref.orderByChild("uid").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (a in p0.children) {
                            val data = a.getValue(Customer::class.java)
                            checkDistance(data?.lat, data?.lon, lat, lon, title, desc)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            })
    }

    private fun checkDistance(
        latAwal: Double?,
        lonAwal: Double?,
        latTujuan: Double?,
        lonTujuan: Double?,
        title: String?,
        desc: String?
    ) {
        dis = CompositeDisposable()
        val origin = "$latAwal, $lonAwal"
        Log.d("AWAL", origin)
        val destination = "$latTujuan, $lonTujuan"
        Log.d("Tujuan", destination)

        dis?.add(
            NetworkModule.getService().actionRoute(
                origin,
                destination,
                "AIzaSyDSMub1sBU2AnCOr8_MKJV2JC_c8I0UOsM"
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val route = it?.routes?.get(0)
                    val distance = route?.legs?.get(0)?.distance

                    hitungJarak(distance, title, desc)
                }, {

                })
        )
    }

    private fun hitungJarak(
        distance: Distance?,
        title: String?,
        desc: String?
    ) {
        val jarak = distance?.value

        val valueBagi = jarak?.div(1000)
        val valueBulat = Math.ceil(valueBagi?.toDouble() ?: 0.0)
        Log.d("JARAK", valueBulat.toString())

        if (valueBulat <= Constant.JARAK_MAKSIMAL) {

            val notif = NotificationHandler(this)
            notif.sendNotification(title, desc)
        }

    }


    private fun setToView(message: JSONObject) {
        val b = Bundle()
        val itemChat = ItemChat()
        insert = InsertQuery(this@FirebaseService)
        itemChat.isMe = "false"
        itemChat.timeStamp = message.getString("timeStamp")
        itemChat.message = message.getString("message")

        insert?.insertChat(itemChat)

        b.putSerializable("message", itemChat)

        val i = Intent()
        i.action = "MESSAGE_CUSTOMER"
        i.putExtra("msg", b)

        val n = NotificationHandler(this)
        n.sendNotification("Pesan Masuk", message.getString("message"))

        sendBroadcast(i)
    }


}
