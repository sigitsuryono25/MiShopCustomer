package com.lauwba.surelabs.mishopcustomer.config

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object HourToMillis {

    fun addExpired(expiredOn: String?): Long {
        val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.ENGLISH)
        val now = sdf.format(Date())

        val date = sdf.parse(now)
        val c = Calendar.getInstance()
        Log.i("simple date format", date.toString())
        c.time = date
        c.add(Calendar.HOUR, expiredOn?.toInt() ?: 0)

        Log.i("Time", c.time.toString())

        return c.timeInMillis
    }

    fun millisToDate(milis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val c = Calendar.getInstance()
        c.timeInMillis = milis
        return sdf.format(c.time)
    }
}