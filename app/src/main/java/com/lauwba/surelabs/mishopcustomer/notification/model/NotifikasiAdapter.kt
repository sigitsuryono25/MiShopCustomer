package com.lauwba.surelabs.mishopcustomer.notification.model


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.shop.detail.DetailMiShopActivity
import kotlinx.android.synthetic.main.notifikasi_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class NotifikasiAdapter(
    private val mValues: MutableList<NotifikasiItem>, private val c: Context?
) : RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>() {

    var color = intArrayOf(R.color.mishop, R.color.miservice, R.color.micar, R.color.mijek, R.color.miexpress)
    var jenis = arrayListOf("Mi-Shop", "Mi-Service", "Mi-Car", "Mi-Bike", "Mi-Express")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notifikasi_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.type.background = c?.resources?.getDrawable(color[item.type ?: 0])
        holder.orderNumber.text = item.idOrder
        holder.deskripsi.text = item.deskripsi
        holder.typeJassa.text = jenis[item.type ?: 0]
        if (item.type == 0) {
            holder.content.onClick {
                //            c?.toast(holder.orderNumber.text)
                c?.startActivity<DetailMiShopActivity>("idOrder" to holder.orderNumber.text)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val type: View = mView.type
        val content: LinearLayout = mView.container
        val orderNumber: TextView = mView.orderNumber
        val deskripsi: TextView = mView.deskripsi
        val typeJassa: TextView = mView.typeJasa

    }
}
