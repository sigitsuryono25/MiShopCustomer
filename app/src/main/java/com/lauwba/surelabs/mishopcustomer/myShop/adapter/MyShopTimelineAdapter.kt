package com.lauwba.surelabs.mishopcustomer.myShop.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.myShop.model.MyShopModel
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.fragment_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class MyShopTimelineAdapter(
    private val mList: MutableList<MyShopModel>?,
    private val mListCustomer: MutableList<Customer>?
) :
    RecyclerView.Adapter<MyShopTimelineAdapter.ViewHolder>() {

    private var c: Context? = null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context)
            .inflate(R.layout.fragment_item, p0, false)
        c = p0.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val data = mList?.get(p1)
        val cus = mListCustomer?.get(p1)
        p0.namaPosting.text = cus?.nama
//        Log.d("NAMA", cus?.nama)
        p0.deskripsi.text = data?.deskripsi
        p0.judul.text = data?.judul
        p0.judul.visibility = View.VISIBLE
        p0.datePosting.text = data?.tanggalPost?.let { HourToMillis.millisToDate(it) }
        p0.hargaPost.text = "Rp. " + ChangeFormat.toRupiahFormat2(data?.harga.toString())
        p0.lokasi.text = data?.lokasi
        p0.idShop.text = data?.idMyShop.toString()
        val number = cus?.telepon
        p0.wa.text = number
//        Log.d("TEL", cus?.telepon)
        p0.wa.onClick {
//            val url = "https://api.whatsapp.com/send?phone=$number"
            val url = "whatsapp://send?phone=$number"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            c?.startActivity(i)
        }

        c?.let {
            Glide.with(it)
                .load(data?.image)
                .into(p0.imagePost)
        }
        c?.let {
            Glide.with(it)
                .load(cus?.fotoCustomer)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(p0.fotouser)
        }

    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val namaPosting: TextView = mView.namaPosting
        val datePosting: TextView = mView.datePosting
        val hargaPost: TextView = mView.hargaPost
        val deskripsi: TextView = mView.deskripsi
        val idShop: TextView = mView.idShop
        val lokasi: TextView = mView.lokasi
        val wa: TextView = mView.wa
        val judul: TextView = mView.judul
        val ambilPenawaran: TextView = mView.ambilPenawaran
        val imagePost: ImageView = mView.imagePost
        val fotouser: ImageView = mView.fotouser
        val container: CardView = mView.container
    }
}