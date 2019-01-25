package com.lauwba.surelabs.mishopcustomer.adapter

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import org.w3c.dom.Text
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.lauwba.surelabs.mishopcustomer.R


class GameAdapter(context: Context, resource: Int, objects : List<GameModel>) : ArrayAdapter<GameModel>(context, resource, objects) {

    inner class ViewHolder{
        lateinit var nama : TextView
        lateinit var url : TextView
        lateinit var img : ImageView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: ViewHolder?
        val itemProfile = getItem(position)

        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_game_adapter, null)
            holder = ViewHolder()
            holder.nama = convertView!!.findViewById<View>(R.id.title) as TextView
            holder.url = convertView!!.findViewById(R.id.url) as TextView
            holder.img = convertView!!.findViewById(R.id.image) as ImageView
            convertView.tag = holder
        } else
            holder = convertView.tag as ViewHolder
        holder.nama.setText(itemProfile!!.nama)
        holder.url.setText(itemProfile!!.url)
        holder.img.setImageResource(itemProfile!!.gambar)
        return convertView
    }

}