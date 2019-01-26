package com.lauwba.surelabs.mishopcustomer

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lauwba.surelabs.mishopcustomer.config.Config

class GameAdapter(nama: Array<String>, img: Array<Int>, url: Array<String>, context: Context) :
    RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    var context: Context
    var nama: Array<String>
    var img: Array<Int>
    var url: Array<String>

    init {
        this.context = context
        this.nama = nama
        this.img = img
        this.url = url
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var v: View
        v = LayoutInflater.from(context).inflate(R.layout.activity_game_adapter, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return nama.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.container.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var i = Intent(context, WebViewActivity::class.java)
                i.putExtra(Config.URL, url.get(p1))
                context.startActivity(i)
            }
        })
        Glide.with(context)
            .load(img.get(p1))
            .into(p0.gambar)
        p0.title.setText(nama.get(p1))
        p0.url.setText(url.get(p1))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var gambar: ImageView
        var title: TextView
        var url: TextView
        var container: RelativeLayout

        init {
            gambar = itemView!!.findViewById(R.id.gambar)
            title = itemView!!.findViewById(R.id.title)
            url = itemView!!.findViewById(R.id.url)
            container = itemView!!.findViewById(R.id.container)
        }
    }


}
