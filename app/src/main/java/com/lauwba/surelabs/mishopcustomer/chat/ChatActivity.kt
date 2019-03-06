package com.lauwba.surelabs.mishopcustomer.chat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.chat.model.ItemChat
import com.lauwba.surelabs.mishopcustomer.chat.model.ListViewAdapter
import com.lauwba.surelabs.mishopcustomer.chat.model.MyAdapter
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    var item: ItemChat? = null
    var myadapter: MyAdapter? = null
    var listViewAdapter: ListViewAdapter? = null
    var listChat: MutableList<ItemChat>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        listChat = mutableListOf()

        sendBtn.setOnClickListener {
            if (send.text.length > 0) {
                item = ItemChat(send.text.toString(), true)
                listChat?.add(item!!)
                send.setText("")
            }

            listViewAdapter =
                ListViewAdapter(this@ChatActivity, R.layout.fragment_item_chat, listChat ?: mutableListOf())
//            message.layoutManager = LinearLayoutManager(this@ChatActivity)
            message.adapter = listViewAdapter
            listViewAdapter?.notifyDataSetChanged()
            message.setSelection(listViewAdapter?.count?.minus(1) ?: 0)
        }
    }
}
