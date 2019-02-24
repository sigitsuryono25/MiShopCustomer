package com.lauwba.surelabs.mishopcustomer.dashboard.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.dashboard.model.ProsesModel
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import kotlinx.android.synthetic.main.item_proses.view.*

class ProsesRecyclerAdapter(
    private val mValues: MutableList<ProsesModel>?,
    private val deskripsi: MutableList<ItemPost>?,
    private val c: Context
) : RecyclerView.Adapter<ProsesRecyclerAdapter.ViewHolder>() {

    val statusDel = arrayListOf("Diterima", "Dipesankan", "Sedang Dijalan", "Selesai")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_proses, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues?.get(position)
        val desk = deskripsi?.get(position)
        holder.tanggalTransaksi.text = item?.tanggalOrder?.let { HourToMillis.millisToDate(it) }
        holder.nominal.text = "Rp. " + ChangeFormat.toRupiahFormat2(item?.price_shop.toString())
        holder.status.text = item?.status_order_shop?.let { statusDel.get(it) }
        holder.keterangan.text = desk?.deskripsi
    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val tanggalTransaksi: TextView = mView.tanggalTransaksi
        val nominal: TextView = mView.nominal
        val keterangan: TextView = mView.keterangan
        val jenis: View = mView.jenis
        val status: TextView = mView.status

    }
}
