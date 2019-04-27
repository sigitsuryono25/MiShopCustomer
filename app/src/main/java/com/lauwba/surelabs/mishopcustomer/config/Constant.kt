package com.lauwba.surelabs.mishopcustomer.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object Constant {
    val INTRO = "intro"
    const val URL_PROVINSI = "http://mishop.server411.tech/index.php/Welcome/prov"
    const val URL_KAB = "http://mishop.server411.tech/index.php/Welcome/kab/"

    val TB_SERVICE_BOOKING = "servicebooking"
    const val CAMERAREQ = 9999
    const val GALERYREQ = 8888
    const val FILEREQ = 7777
    const val MEDIA_TYPE_IMAGE = 100
    const val FOLDERANAME = "MiShop/MyShop"
    const val TB_SHOP = "shop"
    const val TB_MYSHOP = "myshop"
    const val TB_CAR = "micar"
    const val TB_BIKE = "mibike"
    const val TB_EXPRESS = "miexpress"
    const val TB_TARIF = "tarif"
    const val TB_MITRA = "mitra"
    const val TB_INBOX = "inbox"
    const val TB_KRITIK = "kritik"
    const val TB_SERVICE = "service"
    const val TB_CUSTOMER = "customer"
    const val RATING = "rating"
    const val TB_SHOP_ORDER = "shoporder"
    const val TB_SERVICE_ORDER = "serviceorder"

    const val SERVICE = "layanan"

    val database = FirebaseDatabase.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    val storage = FirebaseStorage.getInstance()
    val EMAIL = "email"
    val UID = "uidCustomer"
    val ALAMAT = "alamat"
    const val NAMA_CUSTOMER = "namacustomer"
    const val TOKEN = "token"
    val TELEPON = "telepon"
    val FOTO = "foto"

    val LAT_DEFAULT = -8.6724334
    val LON_DEFAULT = 115.2240954
    const val PLAYER = "Rc2dG7uMlhA"

    val JARAK_MAKSIMAL = 50.0

}