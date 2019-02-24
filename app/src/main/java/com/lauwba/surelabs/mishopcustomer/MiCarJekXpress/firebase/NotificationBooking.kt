package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.firebase

import com.google.gson.annotations.SerializedName

class NotificationBooking {

    @SerializedName("to")
    var token: String? = null
    @SerializedName("data")
    var booking: FirebaseBooking? = null
}