package com.lauwba.surelabs.mishopcustomer.shop

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.shop.adapter.TimeLineAdapter
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import kotlinx.android.synthetic.main.activity_shop.*

class ShopActivity : AppCompatActivity() {
    private var mList: MutableList<ItemPost>? = null
    private var mListMitra: MutableList<ItemMitra>? = null
    private var tarif : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        mList = mutableListOf()
        mListMitra = mutableListOf()

        getDataShop()
        getTarif()
    }

    private fun getTarif() {
        val ref = Constant.database.getReference(Constant.TB_TARIF)
        ref.orderByChild("tipe").equalTo("add")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for(issue in p0.children){
                        val data = issue.getValue(Tarif::class.java)
                        tarif = data?.tarif?.toInt()
                    }
                }
            })
    }

    private fun getDataShop() {
        try {
            val ref = Constant.database.reference
            ref.child(Constant.TB_SHOP).orderByChild("tanggalPost").limitToFirst(10)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(ItemPost::class.java)
                                data?.let { mList?.add(it) }
                                val uid = data?.uid

                                ref.child(Constant.TB_MITRA).orderByChild("uid").equalTo(uid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            for (issues in p0.children) {
                                                val mitraData = issues.getValue(ItemMitra::class.java)
                                                setItemToAdapter(mList, mitraData?.nama_mitra, mitraData?.foto, tarif)
                                            }
                                        }

                                    })
                            }
                        } else {

                        }
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setItemToAdapter(
        mList: MutableList<ItemPost>?,
        nama_mitra: String?,
        foto: String?,
        tarif: Int?
    ) {
        val adapter = TimeLineAdapter(mList, this, nama_mitra, foto, tarif)
        try {
            timeline.layoutManager = LinearLayoutManager(this)
            timeline.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
