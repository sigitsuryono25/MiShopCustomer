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
import com.lauwba.surelabs.mishopcustomer.sqlite.InsertQuery
import com.lauwba.surelabs.mishopcustomer.sqlite.SelectQuery
import com.lauwba.surelabs.mishoplatest.chat.model.FirebaseMessagingMessage
import com.lauwba.surelabs.mishoplatest.chat.model.NotificationMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private var item: ItemChat? = null
    private var listChat: MutableList<ItemChat>? = null
    private var token: ItemMitra? = null
    private var insert: InsertQuery? = null
    private var select: SelectQuery? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        insert = InsertQuery(this@ChatActivity)
        select = SelectQuery(this@ChatActivity)

        try {
            listChat = mutableListOf()

            getDbChat()

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


        sendBtn.setOnClickListener {
            if (send.text.isNotEmpty()) {
                item = ItemChat()
                item?.isMe = "true"
                item?.message = send.text.toString()
                item?.timeStamp = HourToMillis.millisToDate(HourToMillis.millis())
                listChat?.add(item!!)
                send.setText("")
                insert?.insertChat(item)

                val notif = NotificationMessage()
                val base = FirebaseMessagingMessage()


                base.data = item as ItemChat
//            notif.token =
//                "eNVBFoQJVYs:APA91bHrJl6t7arBF3o52zNtmuAoED_YbQEOtro63hvSwxIEOSazTccgaUbL_OsZScY85mGcMUg3ykgkIuPZl-RYBltJNUJFc0MOtj4THeFF7nbzEEYOBazT2f7wnlfsbhudBGe2nRQh"
                notif.token = token?.regid
                notif.message = base

                NetworkModule.getServiceFcm()
                    .actionSendMessage(notif)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
            getDbChat()
        }
    }

    private fun getDbChat() {
        val list = select?.getAllChatList()
        val listViewAdapter = list?.let { ListViewAdapter(this@ChatActivity, R.layout.fragment_item_chat, it) }
        message.adapter = listViewAdapter
        listViewAdapter?.notifyDataSetChanged()
        message.setSelection(listViewAdapter?.count?.minus(1) ?: 0)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                getDbChat()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }
}
