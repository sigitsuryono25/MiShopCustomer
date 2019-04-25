@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.chat.model

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.lauwba.surelabs.mishopcustomer.R


class ListViewAdapter(context: Context, resource: Int, objects: MutableList<ItemChat>) :
    ArrayAdapter<ItemChat>(context, resource, objects) {

    private inner class ViewHolder {
        internal var title: TextView? = null
        internal var timeStamp: TextView? = null
        internal var container: LinearLayout? = null
        internal var messageContainer: LinearLayout? = null
    }

    override fun getView(position: Int, v: View?, parent: ViewGroup): View? {
        var convertView = v
        val holder: ViewHolder?
        val itemProfile = getItem(position)

        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_item_chat, parent, false)
            holder = ViewHolder()
            holder.title = convertView.findViewById(R.id.textCome)
            holder.timeStamp = convertView.findViewById(R.id.timeStamp)
            holder.container = convertView.findViewById(R.id.container)
            holder.messageContainer = convertView.findViewById(R.id.messageContainer)
            convertView.tag = holder
        } else
            holder = convertView.tag as ViewHolder
        if (itemProfile?.isMe.equals("true", true)) {
            holder.container?.gravity = Gravity.END
            holder.messageContainer?.background = parent.context.resources.getDrawable(R.drawable.button_style_orange)
        } else {
            holder.container?.gravity = Gravity.START
            holder.messageContainer?.background = parent.context.resources.getDrawable(R.drawable.button_style_green)
        }
        holder.title?.text = itemProfile?.message
        holder.timeStamp?.text = itemProfile?.timeStamp
        return convertView
    }
}