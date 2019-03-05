package com.lauwba.surelabs.mishopcustomer.firebase

import com.google.gson.annotations.SerializedName

class FirebaseMessagingModel {

    @SerializedName("to")
    var token: String? = null
    @SerializedName("data")
    var data: Any? = null
}