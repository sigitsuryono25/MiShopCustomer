package com.lauwba.surelabs.mishopcustomer.registrasi.model

import java.io.Serializable

class Customer : Serializable {

    var key: String? = null
    var uid: String? = null
    var telepon: String? = null
    var noKTP: String? = null
    var nama: String? = null
    var email: String? = null
    var alamat: String? = null
    var gender: String? = null
    var terdaftar: Long? = null
    var token: String? = null
    var fotoCustomer: String? = null
    var lat: Double? = null
    var lon: Double? = null
}