package com.lauwba.surelabs.mishopcustomer.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.chat.model.ItemChat
import com.lauwba.surelabs.mishopcustomer.chat.model.ListViewAdapter
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishoplatest.chat.model.FirebaseMessagingMessage
import com.lauwba.surelabs.mishoplatest.chat.model.NotificationMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private var item: ItemChat? = null
    private var listViewAdapter: ListViewAdapter? = null
    private var listChat: MutableList<ItemChat>? = null
    private var token: ItemMitra? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        try {
            //receiver
            val filter = IntentFilter("MESSAGE_CUSTOMER")
            registerReceiver(receiver, filter)

            token = intent.getSerializableExtra("token") as ItemMitra

            namaMitra.text = token?.nama_mitra
            Glide.with(this)
                .load(token?.foto)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(fotoMitra)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        listChat = mutableListOf()

        sendBtn.setOnClickListener {
            if (send.text.length > 0) {
                item = ItemChat()
                item?.isMe = true
                item?.message = send.text.toString()
                item?.timeStamp = HourToMillis.millisToDate(HourToMillis.millis())
                listChat?.add(item!!)
                send.setText("")
            }

            listViewAdapter =
                ListViewAdapter(this@ChatActivity, R.layout.fragment_item_chat, listChat ?: mutableListOf())
//            message.layoutManager = LinearLayoutManager(this@ChatActivity)
            message.adapter = listViewAdapter
            listViewAdapter?.notifyDataSetChanged()
            message.setSelection(listViewAdapter?.count?.minus(1) ?: 0)

            val notif = NotificationMessage()
            val base = FirebaseMessagingMessage()


            base.data = item as ItemChat
//            notif.token =
//                "f_7x4QyWUCI:APA91bEjcI2YHDfvFydFgqjD_HNj4OW9qOnljCLJ8NY1gT05vp3PV0JCXuMVuvArIooAdCvfOa1oaiX9M5akjzsw1Rl-AXh_n28OSXUg4MTIjmiaNEOIyc60iRRABRQcBsfcMDlTzWNC"
            notif.token =
                "dexw56Avorc:APA91bHT2uqsBTi6fUFRR6kd31UFBpeXJn0j0DWI0tECenCm_S7QZJqksKGPoPTLrzGGazfS1x8AW82sqMZ7WGypUKeq6Z2KqdCrmJpq9VNn7mDiPt7-dXyVsBXFoEGOJonpCC-n2KJF"
            notif.message = base

            NetworkModule.getServiceFcm()
                .actionSendMessage(notif)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                val b = intent?.getBundleExtra("msg")
                val data = b?.getSerializable("message") as ItemChat
                val messager = data.message
                val timestamp = data.timeStamp

                if (messager?.isNotEmpty() == true) {
                    item = ItemChat()
                    item?.isMe = false
                    item?.message = messager
                    item?.timeStamp = timestamp
                    listChat?.add(item!!)
                }
                listViewAdapter =
                    ListViewAdapter(this@ChatActivity, R.layout.fragment_item_chat, listChat ?: mutableListOf())
                message.adapter = listViewAdapter
                listViewAdapter?.notifyDataSetChanged()
                message.setSelection(listViewAdapter?.count?.minus(1) ?: 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }
}
