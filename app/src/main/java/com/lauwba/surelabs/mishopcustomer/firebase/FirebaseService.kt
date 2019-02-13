package com.lauwba.surelabs.mishopcustomer.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lauwba.surelabs.mishopcustomer.notification.NotificationHandler
import org.json.JSONObject

class FirebaseService : FirebaseMessagingService() {
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.i("TOKEN", p0)
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        if (p0?.data?.isNotEmpty() != false) {
            val data = p0?.data
            val obj = JSONObject(data)
            Log.i("DATA", p0?.data?.toString())
//
            val notif = NotificationHandler(this)
            notif.sendNotification(obj.getString("title"), obj.getString("deskripsi"))

        }

        if (p0?.notification != null) {
            Log.d("onMessageReceived", p0.notification?.body)
        }
    }


}
