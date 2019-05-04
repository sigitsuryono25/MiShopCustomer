package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking

import java.io.Serializable

class CarBikeBooking : Serializable {

    var tanggal: Long? = null
    var deskripsi: String? = null
    var idOrder: String? = null
    var uidCustomer: String? = null
    var namaCustomer: String? = null
    var nomorTelepon: String? = null
    var token: String? = null
    var foto: String? = null
    var lokasiAwal: String? = null
    var latAwal: Double? = null
    var lonAwal: Double? = null
    var lokasiTujuan: String? = null
    var latTujuan: Double? = null
    var lonTujuan: Double? = null
    var harga: Int? = null
    var jarak: String? = null
    var status: Int? = null
    var type: Int? = null
    var driver: String? = null
    var rating: String? = ""

    //express punya

    var namaBarang: String? = null
    var nomorYangDihubungi: String? = null

    init {


    }

}