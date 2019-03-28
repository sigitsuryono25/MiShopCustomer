package com.lauwba.surelabs.mishopcustomer.dashboard.ui.proses


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.dashboard.model.ProsesModel
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import kotlinx.android.synthetic.main.item_proses.view.*

class ProsesAdapter(
    private val mValues: MutableList<ProsesModel>?
) : RecyclerView.Adapter<ProsesAdapter.ViewHolder>() {


    val statusDel = arrayListOf("Diterima", "Dipesankan", "Sedang Dijalan", "Batal", "Selesai")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_proses, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val item = mValues?.get(position)
            holder.tanggalTransaksi.text = item?.tanggalOrder?.let { HourToMillis.millisToDate(it) }
            holder.nominal.text = "Rp. " + ChangeFormat.toRupiahFormat2(item?.price_shop.toString())
            holder.status.text = item?.status_order_shop?.let { statusDel[it] }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val tanggalTransaksi: TextView = mView.tanggalTransaksi
        val nominal: TextView = mView.nominal
        val keterangan: TextView = mView.keterangan
        val jenis: View = mView.jenis
        val status: TextView = mView.status

    }
}