package com.lauwba.surelabs.mishopcustomer.chat.model

import java.io.Serializable

class ItemChat : Serializable {
    var message: String? = null
    var isMe: Boolean? = false
    var timeStamp: String? = null
}