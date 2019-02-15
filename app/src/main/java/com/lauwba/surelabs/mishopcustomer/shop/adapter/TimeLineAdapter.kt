package com.lauwba.surelabs.mishopcustomer.shop.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import kotlinx.android.synthetic.main.fragment_item.view.*


class TimeLineAdapter(
    private val mValues: MutableList<ItemPost>?, var c: Context
) : RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues?.get(position)
        holder.namaPosting.text = item?.namaPosting
        holder.datePosting.text = item?.tanggalPost
        holder.hargaPost.text = item?.harga
        holder.deskripsi.text = item?.deskripsi
        holder.idShop.text = item?.idOrder
        holder.lokasi.text = item?.lokasi

        Glide.with(c)
            .load(item?.fotouser)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.fotouser)
        Glide.with(c)
            .load(item?.foto)
            .into(holder.imagePost)
    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val namaPosting: TextView = mView.namaPosting
        val datePosting: TextView = mView.datePosting
        val hargaPost: TextView = mView.hargaPost
        val deskripsi: TextView = mView.deskripsi
        val idShop: TextView = mView.idShop
        val lokasi: TextView = mView.lokasi
        val imagePost: ImageView = mView.imagePost
        val fotouser: ImageView = mView.fotouser
    }
}
