package com.lauwba.surelabs.mishopcustomer.shop.adapter

import android.util.Log
import com.lauwba.surelabs.mishopcustomer.notification.model.NotifikasiItem

object GetAllData {
    var itemPost: NotifikasiItem? = null
    fun collectidShop(idshop: Map<*, *>): MutableList<NotifikasiItem> {
        val idShop = mutableListOf<NotifikasiItem>()
        for (entry in idshop) {
            val idshopMap = entry.value as Map<*, *>
//            idShop.i(idshopMap["idShop"] as String)
            itemPost = NotifikasiItem()
            itemPost?.idShop = idshopMap["idShop"].toString()
            itemPost?.deskripsi = idshopMap["deskripsi"].toString() + "\n" + idshopMap["harga"].toString()
            idShop.add(itemPost!!)
        }
        Log.i("idSHOP", idShop.toString())
        return idShop
    }


}