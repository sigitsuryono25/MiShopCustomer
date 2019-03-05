package com.lauwba.surelabs.mishopcustomer.myShop.adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.myShop.model.MyShopModel
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.fragment_item.view.*

class MyShopTimelineAdapter(
    private val mList: MutableList<MyShopModel>,
    private val mListCustomer: MutableList<Customer>
) :
    RecyclerView.Adapter<MyShopTimelineAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context)
            .inflate(R.layout.fragment_item, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val data = mList.get(p1)
        val cus = mListCustomer.get(p1)
        p0.namaPosting.text = cus.nama
        p0.datePosting.text = data.tanggalPost?.let { HourToMillis.millisToDate(it) }
        p0.hargaPost.text = "Rp. " + ChangeFormat.toRupiahFormat2(data.harga.toString())
        p0.lokasi.text = data.lokasi
        p0.idShop.text = data.idMyShop.toString()
        p0.wa.text = data.nomorTelepon

    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val namaPosting: TextView = mView.namaPosting
        val datePosting: TextView = mView.datePosting
        val hargaPost: TextView = mView.hargaPost
        val deskripsi: TextView = mView.deskripsi
        val idShop: TextView = mView.idShop
        val lokasi: TextView = mView.lokasi
        val wa: TextView = mView.wa
        val ambilPenawaran: TextView = mView.ambilPenawaran
        val imagePost: ImageView = mView.imagePost
        val fotouser: ImageView = mView.fotouser
        val container: CardView = mView.container
    }
}