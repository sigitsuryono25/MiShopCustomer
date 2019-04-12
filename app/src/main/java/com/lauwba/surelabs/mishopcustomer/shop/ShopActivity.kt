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
import com.lauwba.surelabs.mishopcustomer.services.MyLocationService
import com.lauwba.surelabs.mishopcustomer.shop.adapter.TimeLineAdapter
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import kotlinx.android.synthetic.main.activity_shop.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startService

class ShopActivity : AppCompatActivity() {
    private var mList: MutableList<ItemPost>? = null
    private var tarif: Int? = null
    private var lastTanggal: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        setSupportActionBar(toolbar)
        titleToolbar.text = "Mi Shop"

        mList = mutableListOf()

//        timeline.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (!recyclerView.canScrollVertically(1)) {
//                    getNextData(lastTanggal)
//                }
//            }
//        })
        startService<MyLocationService>()
        getTarif()
    }

    private fun getNextData(tanggal: String?) {
        try {
            val ref = Constant.database.reference
            ref.child(Constant.TB_SHOP).orderByChild("tanggal").limitToFirst(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(ItemPost::class.java)
                                if (data?.statusPost?.equals(
                                        "expired",
                                        true
                                    ) == false || data?.statusPost.isNullOrEmpty()
                                ) {
                                    val exist = mList?.filter {
                                        it.equals(data?.tanggalPost)
                                    }
                                    if (exist?.size == 0) {
                                        data?.let { mList?.add(it) }
                                        lastTanggal = data?.tanggalPost
                                        setItemToAdapter(mList, tarif)
                                    }

                                }
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
        ref.orderByChild("tipe").equalTo("shop")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        val data = issue.getValue(Tarif::class.java)
                        tarif = data?.tarif?.toInt()
                        getDataShop()
                    }
                }
            })
    }

    private fun getDataShop() {
        try {
            val ref = Constant.database.reference
            ref.child(Constant.TB_SHOP).orderByChild("tanggal")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(ItemPost::class.java)
                                if (data?.statusPost?.equals(
                                        "expired",
                                        true
                                    ) == false || data?.statusPost.isNullOrEmpty()
                                ) {
                                    data?.let { mList?.add(it) }
                                    lastTanggal = data?.tanggalPost
                                    setItemToAdapter(mList, tarif)
                                }
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
        tarif: Int?
    ) {
        mList?.sortByDescending {
            it.tanggalPost
        }


        val adapter = TimeLineAdapter(mList, this, tarif)
        try {
            timeline.layoutManager = LinearLayoutManager(this)
            timeline.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
