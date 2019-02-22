package com.lauwba.surelabs.mishopcustomer.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

object Constant {
    const val CAMERAREQ = 9999
    const val GALERYREQ = 8888
    const val FILEREQ = 7777
    const val MEDIA_TYPE_IMAGE = 100
    const val FOLDERANAME = "MiShop"
    const val TB_SHOP = "shop"
    const val TB_SHOP_ORDER = "shoporder"
    const val TB_CAR = "micar"
    const val TB_BIKE = "mibike"
    const val TB_EXPRESS = "miexpress"
    const val TB_SERVICE_ORDER = "serviceorder"
    const val TB_TARIF = "tarif"
    const val TB_MITRA = "mitra"
    const val TB_INBOX = "inbox"
    const val TB_TRANSAKSI_SALDO = "transaksi_saldo"
    const val TB_KRITIK = "kritik"
    const val TB_SERVICE = "service"
    const val TB_CUSTOMER = "customer"


    const val PENAWARAN_MI_EXPRESS = "Penawaran Mi-Express"
    const val PENAWARAN_MI_BIKE = "Penawaran Mi-Bike"
    const val PENAWARAN_MI_CAR = "Penawaran Mi-Car"

    const val INTERNET = "INTERNET"
    const val READ_EXTERNAL_STORAGE = "READ_EXTERNAL_STORAGE"
    const val WRITE_EXTERNAL_STORAGE = "WRITE_EXTERNAL_STORAGE"
    const val ACCESS_FINE_LOCATION = "ACCESS_FINE_LOCATION"
    const val CAMERA = "CAMERA"
    const val FLASHLIGHT = "FLASHLIGHT"
    const val CALL_PHONE = "CALL_PHONE"
    const val ACCESS_COARSE_LOCATION = "ACCESS_COARSE_LOCATION"
    val database = FirebaseDatabase.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    val messaging = FirebaseMessaging.getInstance()
    val storage = FirebaseStorage.getInstance()
    var realPath: String? = null
    val BaseUrlRoute = "https://maps.googleapis.com/maps/api/directions/"
    val BaseUrlFcm = "https://fcm.googleapis.com/"
    val EMAIL = "email"
    val URL = "URL"
    val ID_MITRA = "idMitra"
    val UID = "uid"
    val ALAMAT = "alamat"
    val START = "start"
    val IDSERVICE = "idService"
    val LINK = "link"


}