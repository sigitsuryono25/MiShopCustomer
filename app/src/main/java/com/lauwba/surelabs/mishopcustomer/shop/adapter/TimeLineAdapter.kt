package com.lauwba.surelabs.mishopcustomer.shop.adapter


import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lauwba.ojollauwba.utils.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.lauwba.surelabs.mishopcustomer.shop.model.ShopOrderModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class TimeLineAdapter(
    private val mValues: MutableList<ItemPost>?,
    var c: Context,
    private val nama: String?,
    private val foto: String?,
    private val tarif: Int?
) : RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {

    private var qty: Int? = null
    private var getTarif = Tarif()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues?.get(position)
//        val mitra = mitra?.get(position)
        holder.namaPosting.text = nama
        holder.datePosting.text = item?.tanggalPost?.toLong()?.let { HourToMillis.millisToDate(it) }
        holder.hargaPost.text =
            tarif?.let { item?.harga?.plus(it).toString() }?.let { ChangeFormat.toRupiahFormat2(it) }
        holder.deskripsi.text = item?.deskripsi
        holder.idShop.text = item?.idOrder
        holder.lokasi.text = item?.lokasi

        Glide.with(c)
            .load(foto)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.fotouser)
        Glide.with(c)
            .load(item?.fotoShop)
            .into(holder.imagePost)

        holder.ambilPenawaran.onClick {
//            orderShop(item)
            showQtyAndAddress()
        }
    }

    private fun showQtyAndAddress() {
//        val
    }

    private fun orderShop(item: ItemPost?) {
        val shopOrderModel = ShopOrderModel()
        shopOrderModel.id_order_shop = HourToMillis.millis().toString()
        shopOrderModel.email = Prefs.getString(Constant.EMAIL, "")
        shopOrderModel.uid = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.qty = qty
        shopOrderModel.ship_shop = getTarif.getTarif("add")
        shopOrderModel.price_shop = ChangeFormat.toRupiahFormat2(item?.harga.toString())
        shopOrderModel.lat_cust = 0.0
        shopOrderModel.lon_cust = 0.0
        shopOrderModel.status_order_shop = 0



    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val namaPosting: TextView = mView.namaPosting
        val datePosting: TextView = mView.datePosting
        val hargaPost: TextView = mView.hargaPost
        val deskripsi: TextView = mView.deskripsi
        val idShop: TextView = mView.idShop
        val lokasi: TextView = mView.lokasi
        val ambilPenawaran: TextView = mView.ambilPenawaran
        val imagePost: ImageView = mView.imagePost
        val fotouser: ImageView = mView.fotouser
        val container: CardView = mView.container
    }
}
