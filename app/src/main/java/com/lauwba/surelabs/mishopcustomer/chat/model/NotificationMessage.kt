package com.lauwba.surelabs.mishoplatest.chat.model

import com.google.gson.annotations.SerializedName

class NotificationMessage {

    @SerializedName("to")
    var token: String? = null
    @SerializedName("data")
    var message: FirebaseMessagingMessage? = null

}