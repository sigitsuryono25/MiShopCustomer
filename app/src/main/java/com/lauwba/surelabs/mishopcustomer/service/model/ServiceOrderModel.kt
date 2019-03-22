package com.lauwba.surelabs.mishopcustomer.service.model

class ServiceOrderModel {

    var alamat: String? = null
    var harga: Int? = null
    var idCustomer: String? = null
    var idOrder: String? = null
    var idOrderService: String? = null
    var jadwal: Long? = null
    var jenis: Int? = null
    var lat: Double? = null
    var lon: Double? = null
    var namaCustomer: String? = null
    var ship: Int? = null
    var status: Int? = null
    var tanggal_order: Long? = null
    var uid: String? = null
    var uidCustomer: String? = null

    class MiService {
        var deskripsi: String? = null
        var harga: Int? = null
        var foto: String? = null
        var idOrder: String? = null
        var idMitra: String? = null
        var namaService: String? = null
        var ship_service: Int? = null
        var status: Int? = null
        var tanggal: Long? = null
        var type: Int? = null
        var uid: String? = null
    }
}