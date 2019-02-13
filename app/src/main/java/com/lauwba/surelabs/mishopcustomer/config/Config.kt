package com.lauwba.surelabs.mishopcustomer.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

object Config {

    val HOST = "http://192.168.100.6/Mi-ShopWeb/"
    val REG_URL = HOST + "registrasi-customer"


    //param intent
    val URL = "url"

    //Fieldset
    val NO_TELP = "notelp"
    val PASS = "pass"
    val NAMA = "nama"
    val EMAIL = "email"
    val GENDER = "gender"
    val ALAMAT = "alamat"

    //array result
    val RES = "result"

    val tb_booking = "micarbooking"
    val tb_customer = "customer"
    val tb_shop = "shop"
    val REGISTRATION_COMPLETE = "registrasi complete"
    val database = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()
    val messaging = FirebaseMessaging.getInstance()

    fun databaseInstance(table: String) : DatabaseReference {
        return table.let { FirebaseDatabase.getInstance().getReference(it) }
    }

    fun authInstanceCurrentUser(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}