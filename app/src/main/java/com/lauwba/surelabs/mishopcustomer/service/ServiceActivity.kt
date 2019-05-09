package com.lauwba.surelabs.mishopcustomer.service

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.service.adapter.TimeLineServiceAdapter
import com.lauwba.surelabs.mishopcustomer.service.model.ItemPostService
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import kotlinx.android.synthetic.main.activity_service.*
import kotlinx.android.synthetic.main.toolbar.*

class ServiceActivity : AppCompatActivity() {

    private var mList: MutableList<ItemPostService>? = null
    private var mListMitra: MutableList<ItemMitra>? = null
    private var tarif: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        setSupportActionBar(toolbar)

        titleToolbar.text = "Mi Service"
        Glide.with(this@ServiceActivity).load(R.drawable.ic_group_327)
            .into(iconLayanan)

        mList = mutableListOf()
        mListMitra = mutableListOf()

        getDataService()
        getTarif()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {

        }
    }

    private fun getDataService() {
        try {
            val ref = Constant.database.reference
            ref.child(Constant.TB_SERVICE).orderByChild("tanggal").limitToLast(10)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(ItemPostService::class.java)
                                data?.let { mList?.add(it) }
                                val uid = data?.uid

                                ref.child(Constant.TB_MITRA).orderByChild("uid").equalTo(uid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            for (issues in p0.children) {
                                                val mitraData = issues.getValue(ItemMitra::class.java)
                                                mitraData?.let { mListMitra?.add(it) }
                                                setItemToAdapter(mList, mListMitra, tarif, mitraData)
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

    private fun getTarif() {
        val ref = Constant.database.getReference(Constant.TB_TARIF)
        ref.orderByChild("tipe").equalTo("add")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        val data = issue.getValue(Tarif::class.java)
                        tarif = data?.tarif?.toInt()
                    }
                }
            })
    }

    private fun setItemToAdapter(
        mList: MutableList<ItemPostService>?,
        mitraData: MutableList<ItemMitra>?,
        tarif: Int?,
        mitra: ItemMitra?
    ) {
        val adapter = TimeLineServiceAdapter(mList, this, mitraData, tarif, mitra)
        try {
            val lm = LinearLayoutManager(this)
            lm.stackFromEnd = true
            lm.reverseLayout = true
            rv.layoutManager = lm
            rv.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
