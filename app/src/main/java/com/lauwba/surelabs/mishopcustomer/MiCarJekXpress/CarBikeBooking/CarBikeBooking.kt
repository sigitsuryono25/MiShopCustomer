package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking

import java.io.Serializable

class CarBikeBooking : Serializable {

    var tanggal: String? = null
    var uid: String? = null
    var lokasiAwal: String? = null
    var latAwal: Double? = null
    var lonAwal: Double? = null
    var lokasiTujuan: String? = null
    var latTujuan: Double? = null
    var lonTujuan: Double? = null
    var harga: String? = null
    var jarak: String? = null
    var status: Int? = null
    var type: Int? = 0

    init {


    }

}