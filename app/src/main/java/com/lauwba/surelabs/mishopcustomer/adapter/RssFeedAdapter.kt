package com.lauwba.surelabs.mishopcustomer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.lauwba.surelabs.mishopcustomer.R

class RssFeedAdapter(rssFeedModel: MutableList<RssFeedModel>, context: Context) :
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
        p0.title.setText(itemModel.title)
        p0.pubDate.setText(itemModel.description)
        p0.link.setText(itemModel.link)
        Glide.with(context)
            .load(itemModel.image)
            .into(p0.featurePhotos)

        p0.rss.setOnClickListener {
            Toast.makeText(context, itemModel.title, Toast.LENGTH_SHORT).show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.titleText)
        var pubDate: TextView = itemView.findViewById(R.id.descriptionText)
        var link: TextView = itemView.findViewById(R.id.linkText)
        var featurePhotos: ImageView = itemView.findViewById(R.id.image)
        var rss: RelativeLayout = itemView.findViewById(R.id.rss)
    }
}