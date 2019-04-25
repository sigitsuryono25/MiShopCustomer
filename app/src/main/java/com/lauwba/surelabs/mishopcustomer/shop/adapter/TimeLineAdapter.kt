package com.lauwba.surelabs.mishopcustomer.shop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.firebase.FirebaseBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model.Distance
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.config.InputFilterMinMax
import com.lauwba.surelabs.mishopcustomer.firebase.FirebaseMessagingModel
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.lauwba.surelabs.mishopcustomer.shop.model.ShopOrderModel
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_item.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

@SuppressLint("SetTextI18n")
class TimeLineAdapter(
    private val mValues: MutableList<ItemPost>?,
    var c: Context,
    private val tarif: Int?
) : RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {
    private var data: ItemMitra? = null
    private var dis: CompositeDisposable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues?.get(position)

        val harga = item?.harga
        getNameMitra(item?.uid, holder, item, harga)

        holder.uidMitra.text = item?.uid
        holder.datePosting.text = item?.tanggalPost?.toLong()?.let { HourToMillis.millisToDate(it) }
        holder.hargaPost.text = "Rp. " + ChangeFormat.toRupiahFormat2(harga.toString())
        holder.deskripsi.text = item?.deskripsi
        holder.idShop.text = item?.idOrder
        holder.lokasi.text = item?.lokasi
        holder.wa.visibility = View.GONE
        holder.ambilPenawaran.visibility = View.VISIBLE

        Glide.with(c)
            .load(item?.foto)
            .into(holder.imagePost)

    }

    private fun getNameMitra(
        uid: String?,
        holder: ViewHolder,
        item: ItemPost?,
        harga: Int?
    ): Boolean {
        val flag = false
        val ref = Constant.database.getReference(Constant.TB_MITRA)
        ref.orderByChild("uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        data = issue.getValue(ItemMitra::class.java)
                        holder.namaPosting.text = data?.nama_mitra
                        Glide.with(c)
                            .load(data?.foto)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.fotouser)
                        holder.ambilPenawaran.onClick {
                            //            orderShop(item)
                            showQtyAndAddress(item, data, harga)
                        }
                    }
                }
            })

        return flag
    }

    private fun showQtyAndAddress(
        item: ItemPost?,
        data: ItemMitra?,
        harga: Int?
    ) {
        val dialog = AlertDialog.Builder(c)
        val inflater = c.layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog, null)
        dialog.setView(view)
        dialog.setTitle("Konfirmasi Jumlah")

        val qty = view.findViewById<EditText>(R.id.qty)
        val alamat = view.findViewById<EditText>(R.id.alamat)
        val ongkir = view.findViewById<TextView>(R.id.ongkir)
        val hargaTv = view.findViewById<TextView>(R.id.harga)
        val total = view.findViewById<TextView>(R.id.total)
        val kenaikan = view.findViewById<TextView>(R.id.kenaikan)
        val max = item?.maxPesanan
        qty.hint = "Maksimal Pesanan $max"
        qty.filters = max?.let { InputFilterMinMax(1, it) }?.let { arrayOf<InputFilter>(it) }
        ongkir.text = "Rp. " + ChangeFormat.toRupiahFormat2(tarif.toString())
        hargaTv.text = "Rp. " + ChangeFormat.toRupiahFormat2(harga.toString())
        kenaikan.text = "Rp. " + ChangeFormat.toRupiahFormat2(item?.kenaikan.toString())
//        qty.textChangedListener {
//
//        }
        qty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    val kuantiti = ChangeFormat.clearRp(qty.text.toString()).toInt()
                    val ongkos = ChangeFormat.clearRp(ongkir.text.toString()).toInt()
                    val kenaikanInt = ChangeFormat.clearRp(kenaikan.text.toString()).toInt()
//                    val totl = harga?.let { kuantiti.times(it) }?.plus(ongkos)
                    val totl = harga?.plus(kenaikanInt)?.times(kuantiti)?.plus(ongkos)
                    total.text = "Rp. " + ChangeFormat.toRupiahFormat2(totl.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        alamat.setText(Prefs.getString(Constant.ALAMAT, ""))

        dialog.setPositiveButton(
            "Order"
        ) { _, _ ->

            //            orderShop(item, qty.text.toString().toInt(), alamat.text.toString(), data, harga)
            if (data?.statusAktif == 3)
                c.toast("Mitra Lagi Ada orderan kak. Sabar ya")
            else
                checkDistance(
                    Prefs.getDouble("lat", Constant.LAT_DEFAULT),
                    Prefs.getDouble("lon", Constant.LON_DEFAULT),
                    item?.lat,
                    item?.lon,
                    item, qty.text.toString().toInt(), alamat.text.toString(), data, harga
                )
        }

        dialog.show()

    }

    private fun checkDistance(
        latAwal: Double?,
        lonAwal: Double?,
        latTujuan: Double?,
        lonTujuan: Double?,
        item: ItemPost?,
        qty: Int,
        alamat: String,
        data: ItemMitra?,
        harga: Int?
    ) {
        dis = CompositeDisposable()
        val origin = "$latAwal, $lonAwal"
        val destination = "$latTujuan, $lonTujuan"

        dis?.add(
            NetworkModule.getService().actionRoute(
                origin,
                destination,
                c.getString(R.string.google_maps_key) ?: ""
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val route = it?.routes?.get(0)
                    val overview = route?.overviewPolyline
                    val point = overview?.points
                    val distance = route?.legs?.get(0)?.distance

                    hitungJarak(distance, item, qty, alamat, data, harga)
                }, {

                })
        )
    }

    private fun hitungJarak(
        distance: Distance?,
        item: ItemPost?,
        qty: Int,
        alamat: String,
        data: ItemMitra?,
        harga: Int?
    ) {
        val jarak = distance?.value

        val valueBagi = jarak?.div(1000)
        val valueBulat = Math.ceil(valueBagi?.toDouble() ?: 0.0)
        Log.d("JARAK", valueBulat.toString())

        if (valueBulat > 20.0) {
            c.alert {
                message = "Mohon Maaf kak. " +
                        "Kakak tidak bisa order. " +
                        "Dikarenakan diluar Jarak yang telah ditentukan"
                okButton { }
            }.show()
        } else {
            orderShop(item, qty, alamat, data, harga)
        }

    }

    private fun orderShop(
        item: ItemPost?,
        qty: Int,
        alamat: String,
        data: ItemMitra?,
        harga: Int?
    ) {
        val shopOrderModel = ShopOrderModel()
        val time = HourToMillis.millis()
        shopOrderModel.idOrder = item?.idOrder
        shopOrderModel.id_order_shop = time.toString()
        shopOrderModel.email = Prefs.getString(Constant.EMAIL, "")
        shopOrderModel.uid = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.qty = qty
        shopOrderModel.ship_shop = tarif
        shopOrderModel.price_shop = item?.harga
        shopOrderModel.lat_cust = Prefs.getDouble("lat", Constant.LAT_DEFAULT)
        shopOrderModel.lon_cust = Prefs.getDouble("lon", Constant.LON_DEFAULT)
        shopOrderModel.status_order_shop = 0
        shopOrderModel.uid = item?.uid
        shopOrderModel.uidCustomer = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.tanggalOrder = time
        shopOrderModel.kenaikan = item?.kenaikan

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
