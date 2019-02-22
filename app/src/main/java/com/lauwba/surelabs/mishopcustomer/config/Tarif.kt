package com.lauwba.surelabs.mishopcustomer.config

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Tarif {

    val id: String? = null
    val ket: String? = null
    val tarif: String? = null
    val tipe: String? = null


    fun getTarif(tipe: String?): String {
        var tarif = ""
        val ref = Constant.database.getReference(Constant.TB_TARIF)
        ref.orderByChild("tipe").equalTo(tipe)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        val data = issue.getValue(Tarif::class.java)
                        tarif = data?.tarif.toString()
                    }
                }

            })

        return tarif
    }
}