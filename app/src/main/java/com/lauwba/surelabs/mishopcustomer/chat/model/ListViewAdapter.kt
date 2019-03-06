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
        internal var container: LinearLayout? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        var holder: ViewHolder?
        val itemProfile = getItem(position)

        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_item_chat, null)
            holder = ViewHolder()
            holder.title = convertView.findViewById<View>(R.id.textCome) as TextView
            holder.container = convertView.findViewById<View>(R.id.container) as LinearLayout
            convertView.tag = holder
        } else
            holder = convertView.tag as ViewHolder
        if (itemProfile?.isMe == true) {
            holder.container?.gravity = Gravity.END
            holder.title?.background = parent.context.resources.getDrawable(R.drawable.button_style_white)
        } else {
            holder.container?.gravity = Gravity.START
            holder.title?.background = parent.context.resources.getDrawable(R.drawable.button_style_green)
        }
        holder.title?.text = itemProfile?.message
        return convertView
    }
}