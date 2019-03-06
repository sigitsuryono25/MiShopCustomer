package com.lauwba.surelabs.mishopcustomer.shop.adapter


import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.firebase.FirebaseBooking
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.firebase.FirebaseMessagingModel
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.lauwba.surelabs.mishopcustomer.shop.model.ShopOrderModel
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_item.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick


class TimeLineAdapter(
    private val mValues: MutableList<ItemPost>?,
    var c: Context,
    private val mitra: MutableList<ItemMitra>?,
    private val tarif: Int?
) : RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {

    private var getTarif = Tarif()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues?.get(position)
        val data = mitra?.get(position)
        holder.namaPosting.text = data?.nama_mitra
        holder.uidMitra.text = item?.uid
        holder.datePosting.text = item?.tanggalPost?.toLong()?.let { HourToMillis.millisToDate(it) }
        holder.hargaPost.text = "Rp. " +
                tarif?.let { item?.harga?.plus(it).toString() }?.let { ChangeFormat.toRupiahFormat2(it) }
        holder.deskripsi.text = item?.deskripsi
        holder.idShop.text = item?.idOrder
        holder.lokasi.text = item?.lokasi
        holder.wa.visibility = View.GONE
        holder.ambilPenawaran.visibility = View.VISIBLE

        Glide.with(c)
            .load(data?.foto)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.fotouser)

        Glide.with(c)
            .load(item?.foto)
            .into(holder.imagePost)

        holder.ambilPenawaran.onClick {
            //            orderShop(item)
            showQtyAndAddress(item, data)
        }
    }

    private fun showQtyAndAddress(
        item: ItemPost?,
        data: ItemMitra?
    ) {
        val dialog = AlertDialog.Builder(c)
        val inflater = c.layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog, null)
        dialog.setView(view)
        dialog.setTitle("Konfirmasi Jumlah")

        val qty = view.findViewById<EditText>(R.id.qty)
        val alamat = view.findViewById<EditText>(R.id.alamat)
        alamat.setText(Prefs.getString(Constant.ALAMAT, ""))

        dialog.setPositiveButton("Order", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                orderShop(item, qty.text.toString().toInt(), alamat.text.toString(), data)
            }

        })

        dialog.show()

    }

    private fun orderShop(
        item: ItemPost?,
        qty: Int,
        alamat: String,
        data: ItemMitra?
    ) {
        val shopOrderModel = ShopOrderModel()
        val time = HourToMillis.millis()
        shopOrderModel.idOrder = item?.idOrder
        shopOrderModel.id_order_shop = time.toString()
        shopOrderModel.email = Prefs.getString(Constant.EMAIL, "")
        shopOrderModel.uid = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.qty = qty
        shopOrderModel.ship_shop = getTarif.getTarif("add")
        shopOrderModel.price_shop = item?.harga
        shopOrderModel.lat_cust = 0.0
        shopOrderModel.lon_cust = 0.0
        shopOrderModel.status_order_shop = 0
        shopOrderModel.uid = item?.uid
        shopOrderModel.uidCustomer = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.tanggalOrder = time

        val notif = FirebaseMessagingModel()
        val model = FirebaseBooking()

        model.title = "Mi Shop Order"
        model.deskripsi = "Orderan Mi Shop ke $alamat"
        model.book = shopOrderModel
        model.type = 0

        notif.token = data?.regid
        notif.data = model


        val ref = Constant.database.getReference(Constant.TB_SHOP_ORDER)
        ref.child(time.toString()).setValue(shopOrderModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    NetworkModule.getServiceFcm()
                        .actionSendBook(notif)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .subscribe()

                    c.alert {
                        message = "Orderan Berhasil Dimasukan. Silahkan Lihat Halam Proses untuk melihat Pesanan Anda"
                        title = "Berhasil"
                        okButton {

                        }
                    }.show()
                } else {

                }
            }
            .addOnFailureListener {

            }

    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val namaPosting: TextView = mView.namaPosting
        val datePosting: TextView = mView.datePosting
        val hargaPost: TextView = mView.hargaPost
        val deskripsi: TextView = mView.deskripsi
        val idShop: TextView = mView.idShop
        val lokasi: TextView = mView.lokasi
        val uidMitra: TextView = mView.uidMitra
        val ambilPenawaran: TextView = mView.ambilPenawaran
        val imagePost: ImageView = mView.imagePost
        val fotouser: ImageView = mView.fotouser
        val container: CardView = mView.container
        val wa: TextView = mView.wa
    }
}
