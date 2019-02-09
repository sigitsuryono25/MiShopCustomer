package com.lauwba.surelabs.mishopcustomer.webview.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.webview.WebViewActivity
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.webview.model.GameModel
import org.jetbrains.anko.startActivity

class GameAdapter(gameModel: MutableList<GameModel>, context: Context) :
    RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    var gameModel = gameModel
    var context = context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var v = LayoutInflater.from(p0.context).inflate(R.layout.activity_game_adapter, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return gameModel.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var itemModel = gameModel.get(p1)
        p0.title.setText(itemModel.nama)
        p0.link.setText(itemModel.url)
        Glide.with(context)
            .load(itemModel.gambar)
            .into(p0.featurePhotos)

        p0.rss.setOnClickListener {
            context.startActivity<WebViewActivity>(Config.URL to itemModel.url, "status" to 1)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)
        var link: TextView = itemView.findViewById(R.id.url)
        var featurePhotos: ImageView = itemView.findViewById(R.id.gambar)
        var rss: RelativeLayout = itemView.findViewById(R.id.container)
    }
}