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
import com.lauwba.surelabs.mishopcustomer.dashboard.adapter.InboxItemAdapter
import com.lauwba.surelabs.mishopcustomer.dashboard.adapter.InboxModel
import kotlinx.android.synthetic.main.activity_inbox_fragment.*
import kotlinx.android.synthetic.main.loading.*

class InboxFragment : Fragment() {

    private var mList: MutableList<InboxModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_inbox_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mList = mutableListOf()

        getDataInbox("1")
    }

    private fun getDataInbox(s: String) {
        try {
            loading.visibility = View.VISIBLE
            val ref = Constant.database.getReference(Constant.TB_INBOX)
            ref.orderByChild("to").equalTo(s)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        loading.visibility = View.GONE
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        loading.visibility = View.GONE
                        for (issues in p0.children) {
                            val data = issues.getValue(InboxModel::class.java)
                            data?.let { mList?.add(it) }
                            setToView(mList)
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setToView(mList: MutableList<InboxModel>?) {
        try {
            val adapter = mList?.let { activity?.let { it1 -> InboxItemAdapter(it, it1) } }
            inbox.layoutManager = LinearLayoutManager(activity)
            inbox.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
