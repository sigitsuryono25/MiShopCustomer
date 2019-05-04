@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.notification.model

import android.annotation.SuppressLint
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
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking.CarBikeBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiBikeActivity
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiCarActivity
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiXpressActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.service.model.ItemPostService
import com.lauwba.surelabs.mishopcustomer.service.model.ServiceOrderModel
import com.lauwba.surelabs.mishopcustomer.shop.detail.DetailMiShopActivity
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.notifikasi_item.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class NotifikasiAdapter(
    private val mValues: MutableList<NotifikasiItem>,
    private val c: Context?,
    private val from: Int
) : RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>() {

    var color = intArrayOf(R.color.mishop, R.color.micar, R.color.mibike, R.color.miexpress, R.color.miservice)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notifikasi_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.tanggalTransaksi.text = item.tanggal?.let { HourToMillis.millisToDate(it) }
        holder.nominal.text = "Rp. " + ChangeFormat.toRupiahFormat2(item.harga.toString())
        holder.jenis.background = c?.resources?.getDrawable(color[from])
        when (from) {
            0 -> {
                holder.shopDetail.visibility = View.VISIBLE
                getDetail(item, holder)
                holder.detail.onClick {
                    c?.startActivity<DetailMiShopActivity>("idOrder" to item.idOrder)
                }
            }
            1 -> {
                holder.carBikeExpress.visibility = View.VISIBLE
                getDetailCarBikExpress(item, holder, Constant.TB_CAR)
                holder.detail.onClick {
                    c?.startActivity<MiCarActivity>("idOrder" to item.idOrder, "driver" to item.driver)
                }
            }
            2 -> {
                holder.carBikeExpress.visibility = View.VISIBLE
                getDetailCarBikExpress(item, holder, Constant.TB_BIKE)
                holder.detail.onClick {
                    c?.startActivity<MiBikeActivity>("idOrder" to item.idOrder, "driver" to item.driver)
                }
            }
            3 -> {
                holder.carBikeExpress.visibility = View.VISIBLE
                getDetailCarBikExpress(item, holder, Constant.TB_EXPRESS)
                holder.detail.onClick {
                    c?.startActivity<MiXpressActivity>("idOrder" to item.idOrder, "driver" to item.driver)
                }
            }
            4 -> {
                holder.serviceDetail.visibility = View.VISIBLE
                getDetailService(item, holder)
                holder.detail.onClick {
                    getDetailService(item)
                }
            }
        }
    }

    private fun getDetail(
        item: NotifikasiItem?,
        holder: ViewHolder
    ) {
        val idOrder = item?.idOrder
        val ref = Constant.database.getReference(Constant.TB_SHOP)
        ref.orderByChild("idOrder").equalTo(idOrder)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issues in p0.children) {
                            val detailShop = issues.getValue(ItemPost::class.java)
                            holder.isiPesanan.text = detailShop?.deskripsi
                            val hargaShow = detailShop?.kenaikan?.let { detailShop.harga?.plus(it) }
                            holder.nominal.text = ChangeFormat.toRupiahFormat2(hargaShow.toString())
                            holder.ongkir.text = ChangeFormat.toRupiahFormat2(detailShop?.ongkos.toString())
                            holder.tanggalTransaksi.text =
                                detailShop?.tanggalPost?.let { HourToMillis.millisToDate(it.toLong()) }
                            holder.lokasiShop.text = detailShop?.lokasi
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun getDetailService(
        item: NotifikasiItem?,
        holder: ViewHolder
    ) {
        val idOrder = item?.idOrder
        val ref = Constant.database.getReference(Constant.TB_SERVICE)
        ref.orderByChild("idOrder").equalTo(idOrder)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issues in p0.children) {
                            val service = issues.getValue(ItemPostService::class.java)
                            holder.biayaJasa.text =
                                ChangeFormat.toRupiahFormat2(service?.ship_service.toString())
                            holder.nominalService.text =
                                ChangeFormat.toRupiahFormat2(service?.harga.toString())
                            holder.deskripsiService.text = service?.deskripsi
                            holder.isiLayanan.text = service?.namaService
                            holder.tanggalTransaksi.text = service?.tanggal?.let {
                                HourToMillis.millisToDate(
                                    it
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun getDetailCarBikExpress(
        item: NotifikasiItem?,
        holder: ViewHolder,
        get: String
    ) {
        val idOrder = item?.idOrder
        val ref = Constant.database.getReference(get)
        ref.orderByChild("idOrder").equalTo(idOrder)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issues in p0.children) {
                            val detail = issues.getValue(CarBikeBooking::class.java)
                            holder.tanggalTransaksi.text = detail?.tanggal?.let { HourToMillis.millisToDate(it) }
                            holder.lokasi.text = detail?.deskripsi
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun getDetailService(idOrder: NotifikasiItem) {
        val orderan = idOrder.idOrder
        val ref = Constant.database.getReference(Constant.TB_SERVICE)
        ref.orderByChild("idOrder").equalTo(orderan)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issue in p0.children) {
                            val data = issue.getValue(ItemPostService::class.java)
                            getitemMitra(data, data?.uid)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            })
    }

    private fun getitemMitra(itemPostService: ItemPostService?, uid: String?) {
        val ref = Constant.database.getReference(Constant.TB_MITRA)
        ref.orderByChild("uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issue in p0.children) {
                            val data = issue.getValue(ItemMitra::class.java)
                            showAlert(data, itemPostService)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun getItemCount(): Int = mValues.size

    private fun showAlert(
        mitra: ItemMitra?,
        item: ItemPostService?
    ) {
        val layout = LayoutInflater.from(c)
        val nomorTelpon = mitra?.no_tel
        val v = layout.inflate(R.layout.layout_mitra, null)
        val alertDialog = c?.let { AlertDialog.Builder(it) }
        alertDialog?.setView(v)
        val foto = v.findViewById<ImageView>(R.id.fotomitra)
        val nama = v.findViewById<TextView>(R.id.namaMitra)
        val tel = v.findViewById<TextView>(R.id.nomorTelepon)
        val wa = v.findViewById<TextView>(R.id.wa)
        val sms = v.findViewById<TextView>(R.id.sms)
        val telepon = v.findViewById<TextView>(R.id.call)
        val booking = v.findViewById<Button>(R.id.booking)
        nama.text = mitra?.nama_mitra
        tel.text = nomorTelpon
        c?.let {
            Glide.with(it)
                .load(mitra?.foto)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(foto)
        }
        wa.onClick {
            try {
                //            val url = "whatsapp://send?phone=$nomorTelpon"
                val url = "https://api.whatsapp.com/send?phone=$nomorTelpon"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                c?.startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        booking.onClick {
            orderService(item)
        }

        sms.onClick {
            nomorTelpon?.let { it1 -> c?.sendSMS(it1) }
        }

        telepon.onClick {
            nomorTelpon?.let { it1 -> c?.makeCall(it1) }
        }



        alertDialog?.create()?.show()
    }

    private fun orderService(item: ItemPostService?) {
        val ref = Constant.database.getReference(Constant.TB_SERVICE_ORDER)
        val idOrderServiceOrder = HourToMillis.millis().toString()
        val booking = ServiceOrderModel()

        booking.alamat = Prefs.getString(Constant.ALAMAT, "")
        booking.harga = item?.harga
        booking.idOrder = item?.idOrder
        booking.idOrderService = idOrderServiceOrder
        booking.jadwal = 0
        booking.jenis = item?.namaService
        booking.lat = Prefs.getDouble("lat", Constant.LON_DEFAULT)
        booking.lon = Prefs.getDouble("lon", Constant.LAT_DEFAULT)
        booking.namaCustomer = item?.namaService
        booking.ship = item?.ship_service
        booking.status = 1
        booking.tanggal_order = HourToMillis.millis()
        booking.uid = item?.uid
        booking.uidCustomer = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)

        ref.child(idOrderServiceOrder).setValue(booking)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    c?.alert {
                        message = "Berhasil Di Booking\nMitra akan menghubungi kakak segera setelah bookingan diterima"
                        isCancelable = false
                        okButton { }
                    }?.show()
            }

    }


    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val detail: CardView = mView.detail

        val serviceDetail: LinearLayout = mView.serviceDetail
        val shopDetail: LinearLayout = mView.shopDetail
        val carBikeExpress: LinearLayout = mView.carBikeExpress

        val tanggalTransaksi: TextView = mView.tanggalTransaksi
        val jenis: View = mView.jenis
        val isiPesanan: TextView = mView.isiPesanan
        val lokasiShop: TextView = mView.lokasiShop
        val nominal: TextView = mView.nominal
        val ongkir: TextView = mView.ongkir

        val lokasi: TextView = mView.lokasi

        val isiLayanan: TextView = mView.isiLayanan
        val deskripsiService: TextView = mView.deskripsiService
        val nominalService: TextView = mView.nominalService
        val biayaJasa: TextView = mView.biayaJasa


    }
}
