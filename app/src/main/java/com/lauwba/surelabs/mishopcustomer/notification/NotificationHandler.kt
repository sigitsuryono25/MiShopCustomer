package com.lauwba.surelabs.mishopcustomer.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.dashboard.DashboardActivity

class NotificationHandler(var c: Context?) {

    fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(c, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("notif", 1024)
        val pendingIntent = PendingIntent.getActivity(
            c, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = c?.getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val defaultSoundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + c?.packageName + "/" + R.raw.notification)
        val notificationBuilder = channelId?.let {
            c?.let { it1 ->
                NotificationCompat.Builder(it1, it)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(null, 0)
                    .setContentIntent(pendingIntent)
            }
        }


        try {
            val r = RingtoneManager.getRingtone(c, defaultSoundUri)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val notificationManager = c?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setSound(null, null)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            0 // ID of notification
            , notificationBuilder?.build()
        )
    }
}