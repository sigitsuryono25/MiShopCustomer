package com.lauwba.surelabs.mishopcustomer.service.adapter


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
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.service.model.ItemPostService
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_item.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast


class TimeLineServiceAdapter(
    private val mValues: MutableList<ItemPostService>?,
    var c: Context,
    private val mitra: MutableList<ItemMitra>?,
    private val tarif: Int?
) : RecyclerView.Adapter<TimeLineServiceAdapter.ViewHolder>() {

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
        holder.datePosting.text = item?.tanggal?.toLong()?.let { HourToMillis.millisToDate(it) }
        val harga = item?.ship_service?.let { item.harga?.plus(it) }
        holder.hargaPost.text = "Rp. " + ChangeFormat.toRupiahFormat2(harga.toString())
        holder.deskripsi.text = item?.deskripsi
        holder.idShop.text = item?.idOrder
        holder.lokasi.visibility = View.GONE
        holder.wa.visibility = View.GONE
        holder.ambilPenawaran.text = "Ambil dan Atur"
        holder.ambilPenawaran.visibility = View.VISIBLE

        Glide.with(c)
            .load(data?.foto)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.fotouser)

        Glide.with(c)
            .load(item?.foto)
            .into(holder.imagePost)

        holder.ambilPenawaran.onClick {
            item?.idOrder?.let { it1 -> c.toast(it1) }
            aturJadwal(item?.idOrder)
        }
    }

    private fun aturJadwal(idOrder: String?) {
        val dialog = AlertDialog.Builder(c)
        val inflater = c.layoutInflater
        val view = inflater.inflate(R.layout.atur_jadwal_service, null)
        dialog.setView(view)
        dialog.setTitle("Konfirmasi Berapa Kali")

        val berapaKali = view.findViewById<EditText>(R.id.berapaKali)

        dialog.setPositiveButton("Order", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                orderService(
                    idOrder,
                    berapaKali.text.toString().toInt(),
                    Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
                )
            }

        })

        dialog.show()
    }

    private fun orderService(idOrder: String?, kali: Int, uidCustomer: String) {
        val ref = Constant.database.getReference(Constant.TB_SERVICE)
        ref.child(idOrder ?: "").child("kali").setValue(kali)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.child(idOrder ?: "").child("uidCustomer").setValue(uidCustomer)
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                                c.alert {
                                    message = "Orderan Berhasil Di Masukkan"
                                    okButton { }
                                }.show()
                        }
                }
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
