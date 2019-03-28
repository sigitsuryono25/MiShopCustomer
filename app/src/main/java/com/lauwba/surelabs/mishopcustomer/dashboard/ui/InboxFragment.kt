package com.lauwba.surelabs.mishopcustomer.dashboard.ui

import android.content.ContentResolver
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.dashboard.adapter.InboxItemAdapter
import com.lauwba.surelabs.mishopcustomer.dashboard.model.InboxModel
import com.lauwba.surelabs.mishopcustomer.sqlite.InsertQuery
import com.lauwba.surelabs.mishopcustomer.sqlite.SelectQuery
import kotlinx.android.synthetic.main.activity_inbox_fragment.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.loading.*

class InboxFragment : Fragment() {

    private var mList: MutableList<InboxModel>? = null

    private var insert: InsertQuery? = null
    private var select: SelectQuery? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_inbox_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mList = mutableListOf()

        insert = activity?.let { InsertQuery(it) }
        select = activity?.let { SelectQuery(it) }

        getDataInbox()
    }

    private fun getDataInbox() {
        var flag = true
        try {
            loading.visibility = View.VISIBLE
            val ref = Constant.database.getReference(Constant.TB_INBOX)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    loading.visibility = View.GONE
                    for (issues in p0.children) {
                        val data = issues.getValue(InboxModel::class.java)
                        val insertData = InboxModel()
                        insertData.id = data?.id
                        insertData.to = data?.to
                        insertData.foto = data?.foto
                        insertData.message = data?.message
                        insertData.broadcaston = data?.broadcaston
                        if (!data?.to.equals("1", true)) {
                            data?.let { mList?.add(it) }
                            setToView(mList)

                            if (insert?.InsertInbox(insertData) == true) {
                                setBadges(View.VISIBLE, flag)
                                flag = false
                            } else {
                                setBadges(View.GONE, false)
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setToView(mList: MutableList<InboxModel>?) {
        mList?.sortByDescending {
            it.broadcaston
        }
        try {
            val adapter = mList?.let { activity?.let { it1 -> InboxItemAdapter(it, it1) } }
            inbox.layoutManager = LinearLayoutManager(activity)
            inbox.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setBadges(visible: Int, flag: Boolean) {
        val bottomNavigationMenuView = activity?.navigation?.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(2)
        val itemView = v as BottomNavigationItemView

        try {
            val badge = LayoutInflater.from(activity)
                .inflate(R.layout.badges, itemView, true)
            val count = badge.findViewById<TextView>(R.id.notifications)
            count.visibility = visible
            if (flag) {
                val defaultSoundUri =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity?.packageName + "/" + R.raw.notification)
                val r = RingtoneManager.getRingtone(activity, defaultSoundUri)
                r.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
