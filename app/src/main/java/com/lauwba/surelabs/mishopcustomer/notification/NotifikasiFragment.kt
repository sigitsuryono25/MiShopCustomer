package com.lauwba.surelabs.mishopcustomer.notification

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
import com.lauwba.surelabs.mishopcustomer.config.Config
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
        val key = Config.database.getReference()
        key.child(Config.tb_shop).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
//                list = GetAllData.collectidShop(p0.value as Map<*, *>)
                list?.let { list?.removeAll(it) }
                for (data in p0.children) {
                    val notifikasiItem = data.getValue(NotifikasiItem::class.java)
                    notifikasiItem?.let { list?.add(it) }
                    setToAdapter(list)
                }
            }
        })
    }

    private fun setToAdapter(list: MutableList<NotifikasiItem>?) {
        val adapter = list?.let { NotifikasiAdapter(it, activity, 0) }
        notifikasi.layoutManager = LinearLayoutManager(activity)
        notifikasi.adapter = adapter
        loading.visibility = View.GONE
    }
}
