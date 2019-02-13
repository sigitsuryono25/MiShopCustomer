package com.lauwba.surelabs.mishopcustomer.notification.model


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lauwba.surelabs.mishopcustomer.R
import kotlinx.android.synthetic.main.notifikasi_item.view.*

class NotifikasiAdapter(
    private val mValues: MutableList<NotifikasiItem>, private val c: Context?, private val type: Int?
) : RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>() {

    var color = intArrayOf(R.color.mishop, R.color.miservice, R.color.micar, R.color.mijek, R.color.miexpress)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notifikasi_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.type.background = c?.resources?.getDrawable(color[type ?: 0])
        holder.orderNumber.text = item.idShop
        holder.typeJassa.text = item.type
        holder.deskripsi.text = item.deskripsi
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val type: View = mView.type
        val orderNumber: TextView = mView.orderNumber
        val typeJassa: TextView = mView.typeJasa
        val deskripsi: TextView = mView.deskripsi

    }
}
