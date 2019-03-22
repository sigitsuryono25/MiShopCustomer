package com.lauwba.surelabs.mishopcustomer.service.adapter


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
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
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.service.model.ItemPostService
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import kotlinx.android.synthetic.main.fragment_item.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sendSMS


class TimeLineServiceAdapter(
    private val mValues: MutableList<ItemPostService>?,
    var c: Context,
    private val mitra: MutableList<ItemMitra>?,
    private val tarif: Int?,
    private val mitraData: ItemMitra?
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
        holder.ambilPenawaran.text = "Hubungi Mitra"
        holder.ambilPenawaran.visibility = View.VISIBLE

        Glide.with(c)
            .load(data?.foto)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.fotouser)

        Glide.with(c)
            .load(item?.foto)
            .into(holder.imagePost)

        holder.ambilPenawaran.onClick {
            showAlert(mitraData)
        }
    }

    private fun showAlert(mitra: ItemMitra?) {
        val layout = LayoutInflater.from(c)
        val nomorTelpon = mitra?.no_tel
        val v = layout.inflate(R.layout.layout_mitra, null)
        val alertDialog = AlertDialog.Builder(c)
        alertDialog.setView(v)
        val foto = v.findViewById<ImageView>(R.id.fotomitra)
        val nama = v.findViewById<TextView>(R.id.namaMitra)
        val tel = v.findViewById<TextView>(R.id.nomorTelepon)
        val wa = v.findViewById<TextView>(R.id.wa)
        val sms = v.findViewById<TextView>(R.id.sms)
        val telepon = v.findViewById<TextView>(R.id.call)
        nama.text = mitra?.nama_mitra
        tel.text = nomorTelpon
        Glide.with(c)
            .load(mitra?.foto)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(foto)
        wa.onClick {
            val url = "whatsapp://send?phone=$nomorTelpon"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            c.startActivity(i)
        }

        sms.onClick {
            nomorTelpon?.let { it1 -> c.sendSMS(it1) }
        }

        telepon.onClick {
            nomorTelpon?.let { it1 -> c.makeCall(it1) }
        }

        alertDialog.create().show()
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
