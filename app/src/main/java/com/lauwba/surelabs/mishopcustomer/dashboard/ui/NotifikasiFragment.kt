package com.lauwba.surelabs.mishopcustomer.dashboard.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.notification.model.NotifikasiAdapter
import com.lauwba.surelabs.mishopcustomer.notification.model.NotifikasiItem
import kotlinx.android.synthetic.main.activity_notifikasi.*
import kotlinx.android.synthetic.main.loading.*

class NotifikasiFragment : Fragment() {

    private var list: MutableList<NotifikasiItem>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_notifikasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        list = mutableListOf()
    }

    private fun getData() {
        val shop = Constant.database.reference
        try {
            shop.child(Constant.TB_SHOP).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        for (issue in p0.children) {
                            val data = issue.getValue(NotifikasiItem::class.java)
                            data?.let { list?.add(it) }
                            getServiceData(list)
                        }
                    } else {
                        getServiceData(list)
                    }
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getServiceData(list: MutableList<NotifikasiItem>?) {
        val service = Constant.database.reference
        service.child(Constant.TB_SERVICE).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (issue in p0.children) {
                        val data = issue.getValue(NotifikasiItem::class.java)
                        data?.let { list?.add(it) }
                        getExpressData(list)
                    }
                } else {
                    getExpressData(list)
                }
            }
        })
    }

    private fun getExpressData(list: MutableList<NotifikasiItem>?) {
        val express = Constant.database.reference
        express.child(Constant.TB_EXPRESS).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (issue in p0.children) {
                        val data = issue.getValue(NotifikasiItem::class.java)
                        data?.let { list?.add(it) }
                        getBikeData(list)
                    }
                } else {
                    getBikeData(list)
                }
            }
        })
    }

    private fun getBikeData(list: MutableList<NotifikasiItem>?) {
        val bike = Constant.database.reference
        bike.child(Constant.TB_BIKE).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (issue in p0.children) {
                        val data = issue.getValue(NotifikasiItem::class.java)
                        data?.let { list?.add(it) }
                        getCarData(list)
                    }
                } else {
                    getCarData(list)
                }
            }
        })
    }

    private fun getCarData(list: MutableList<NotifikasiItem>?) {
        val car = Constant.database.reference
        car.child(Constant.TB_CAR).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (issue in p0.children) {
                        val data = issue.getValue(NotifikasiItem::class.java)
                        data?.let { list?.add(it) }
                        setToAdapter(list)
                    }
                } else {
                    setToAdapter(list)
                }
            }
        })
    }

    private fun setToAdapter(list: MutableList<NotifikasiItem>?) {
        try {
            val adapter = list?.let { NotifikasiAdapter(it, activity) }
            notifikasi.layoutManager = LinearLayoutManager(activity)
            notifikasi.adapter = adapter
            loading.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
