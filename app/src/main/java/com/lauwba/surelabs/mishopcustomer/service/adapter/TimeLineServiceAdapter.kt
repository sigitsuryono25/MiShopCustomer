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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.firebase.FirebaseBooking
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.firebase.FirebaseMessagingModel
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.service.model.ItemPostService
import com.lauwba.surelabs.mishopcustomer.service.model.ServiceOrderModel
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
            showAlert(data, item)
        }
    }

    private fun showAlert(
        mitra: ItemMitra?,
        item: ItemPostService?
    ) {
        val layout = LayoutInflater.from(c)
        val nomorTelpon = mitra?.no_tel
        val v = layout.inflate(R.layout.layout_mitra, null)
        val alertDialog = AlertDialog.Builder(c)
        alertDialog.setView(v)
        alertDialog
        val foto = v.findViewById<ImageView>(R.id.fotomitra)
        val nama = v.findViewById<TextView>(R.id.namaMitra)
        val tel = v.findViewById<TextView>(R.id.nomorTelepon)
        val wa = v.findViewById<TextView>(R.id.wa)
        val sms = v.findViewById<TextView>(R.id.sms)
        val telepon = v.findViewById<TextView>(R.id.call)
        val booking = v.findViewById<Button>(R.id.booking)
        nama.text = mitra?.nama_mitra
        tel.text = nomorTelpon
        Glide.with(c)
            .load(mitra?.foto)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(foto)
        wa.onClick {
            //            val url = "whatsapp://send?phone=$nomorTelpon"
            val url = "https://api.whatsapp.com/send?phone=${nomorTelpon?.replace("+", "")}"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            c.startActivity(i)
        }

        booking.onClick {
            orderService(item, alertDialog)
        }

        sms.onClick {
            nomorTelpon?.let { it1 -> c.sendSMS(it1) }
        }

        telepon.onClick {
            nomorTelpon?.let { it1 -> c.makeCall(it1) }
        }



        alertDialog.create().show()
    }

    private fun orderService(
        item: ItemPostService?,
        alertDialog: AlertDialog.Builder
    ) {
        val ref = Constant.database.getReference(Constant.TB_SERVICE_BOOKING)
        val idOrderServiceOrder = HourToMillis.millis().toString()
        val booking = ServiceOrderModel()

        booking.idOrder = item?.idOrder
        booking.uid = item?.uid
        booking.uidCustomer = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)

        ref.child(idOrderServiceOrder).setValue(booking)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    pushNotif(item?.uid, booking, alertDialog)
            }

    }

    private fun pushNotif(
        uid: String?,
        booking: ServiceOrderModel,
        alertDialog: AlertDialog.Builder
    ) {
        val ref = Constant.database.getReference(Constant.TB_MITRA)
        uid?.let {
            ref.orderByChild("uid").equalTo(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            for (issue in p0.children) {
                                val data = issue.getValue(ItemMitra::class.java)
                                val notif = FirebaseMessagingModel()
                                val model = FirebaseBooking()

                                model.title = "Mi Service"
                                model.deskripsi =
                                    "Bookingan Mi Service oleh ${Prefs.getString(Constant.NAMA_CUSTOMER, "")}"
                                model.book = booking
                                model.type = 4

                                notif.token = data?.regid
                                notif.data = model

                                NetworkModule.getServiceFcm()
                                    .actionSendBook(notif)
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .observeOn(Schedulers.io())
                                    .subscribe()

                                c.alert {
                                    message =
                                        "Berhasil Di Booking\nMitra akan menghubungi kakak segera setelah bookingan diterima"
                                    isCancelable = false
                                    okButton { alertDialog.create().hide() }
                                }.show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
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
