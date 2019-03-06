package com.lauwba.surelabs.mishopcustomer.chat.model

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lauwba.surelabs.mishopcustomer.R
import kotlinx.android.synthetic.main.fragment_item_chat.view.*

class MyAdapter(var message: MutableList<ItemChat>?) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val li = LayoutInflater.from(p0.context)
        var v = li.inflate(R.layout.fragment_item_chat, p0, false)

        return ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return message?.size ?: 0
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val item = message?.get(p1)
        p0.message.text = item?.message
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message = itemView.textCome
    }

}