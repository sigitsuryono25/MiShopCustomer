package com.lauwba.surelabs.mishopcustomer.dashboard.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.dashboard.model.RssFeedModel
import com.lauwba.surelabs.mishopcustomer.webview.WebViewActivity
import org.jetbrains.anko.startActivity

class RssFeedAdapter(rssFeedModel: ArrayList<RssFeedModel>, context: Context) :
    RecyclerView.Adapter<RssFeedAdapter.ViewHolder>() {

    var rssFeedModel = rssFeedModel
    var context = context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var v = LayoutInflater.from(p0.context).inflate(R.layout.item_rss_feed, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return rssFeedModel.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var itemModel = rssFeedModel.get(p1)
        p0.title.text = itemModel.title
        p0.link.text = itemModel.link
        Glide.with(context)
            .load(itemModel.image)
            .into(p0.featurePhotos)

        p0.rss.setOnClickListener {
            context.startActivity<WebViewActivity>(Config.URL to itemModel.link, "status" to 2)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.titleText)
        var pubDate: TextView = itemView.findViewById(R.id.descriptionText)
        var link: TextView = itemView.findViewById(R.id.linkText)
        var featurePhotos: ImageView = itemView.findViewById(R.id.image)
        var rss: CardView = itemView.findViewById(R.id.rss)
    }
}