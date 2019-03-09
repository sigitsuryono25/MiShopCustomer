package com.lauwba.surelabs.mishopcustomer.dashboard.ui.notification.ui


import android.app.ProgressDialog
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
import kotlinx.android.synthetic.main.fragment_content_notification.*

class MiShopNotification : Fragment() {

    private var list: MutableList<NotifikasiItem>? = null
    private var pd: ProgressDialog? = null

    companion object {
        var kind: String? = null
        fun newInstance(k: String?): Fragment {
            kind = k
            return MiShopNotification()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = mutableListOf()
        getData()
    }

    private fun getData() {
        pd = ProgressDialog.show(activity, "", "Memuat notifikasi...")
        val shop = Constant.database.reference
        try {
            kind?.let {
                shop.child(it).limitToLast(10).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        pd?.dismiss()
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(NotifikasiItem::class.java)
                                data?.let { list?.add(it) }
                                setToAdapter(list)
                            }
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setToAdapter(list: MutableList<NotifikasiItem>?) {
        list?.sortByDescending {
            it.idOrder
        }
        try {
            val adapter = list?.let { NotifikasiAdapter(it, activity) }
            notifikasiList.layoutManager = LinearLayoutManager(activity)
            notifikasiList.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}