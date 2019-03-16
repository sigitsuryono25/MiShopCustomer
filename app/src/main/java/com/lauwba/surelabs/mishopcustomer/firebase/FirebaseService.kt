package com.lauwba.surelabs.mishopcustomer.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lauwba.surelabs.mishopcustomer.chat.model.ItemChat
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.notification.NotificationHandler
import com.pixplicity.easyprefs.library.Prefs
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
            Log.i("DATA", p0?.data?.toString())

            try {

                if (!Prefs.getBoolean(Constant.SERVICE, false)) {

                } else {
                    if (data.toString().contains("message", true)) {
                        val message = JSONObject(data?.get("data"))
                        setToView(message)
                    } else {
                        val obj = JSONObject(data)
                        val notif = NotificationHandler(this)
                        notif.sendNotification(obj.getString("title"), obj.getString("deskripsi"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (p0?.notification != null) {
            Log.d("onMessageReceived", p0.notification?.body)
        }
    }


    private fun setToView(message: JSONObject) {
        val b = Bundle()
        val itemChat = ItemChat()
        itemChat.isMe = false
        itemChat.timeStamp = message.getString("timeStamp")
        itemChat.message = message.getString("message")

        b.putSerializable("message", itemChat)

        val i = Intent()
        i.action = "MESSAGE_CUSTOMER"
        i.putExtra("msg", b)

        sendBroadcast(i)
    }


}
