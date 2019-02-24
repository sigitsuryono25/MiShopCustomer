package com.lauwba.surelabs.mishopcustomer.dashboard.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import kotlinx.android.synthetic.main.inbox_item.view.*

class InboxItemAdapter(
    private val mValues: List<InboxModel>, private val context: Context
) : RecyclerView.Adapter<InboxItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inbox_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.message.text = item.message
        holder.postOn.text = item.broadcaston?.let { HourToMillis.millisToDate(it) }
        if (!item.foto.equals("")) {
            holder.imageFeature.visibility = View.VISIBLE
            Glide.with(context)
                .load(item.foto)
                .into(holder.imageFeature)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val message: TextView = mView.message
        val postOn: TextView = mView.postOn
        val imageFeature: ImageView = mView.imageFeature
    }
}
