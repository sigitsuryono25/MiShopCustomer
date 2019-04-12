package com.lauwba.surelabs.mishopcustomer.shop.detail

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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
import com.lauwba.surelabs.mishopcustomer.config.InputFilterMinMax
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.dashboard.DashboardActivity
import com.lauwba.surelabs.mishopcustomer.firebase.FirebaseMessagingModel
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.lauwba.surelabs.mishopcustomer.shop.model.ShopOrderModel
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail_mi_shop.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class DetailMiShopActivity : AppCompatActivity() {
    private var tarif: Int? = null
    private var detail: ItemPost? = null
    private var mitraData: ItemMitra? = null
    private var harga: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mi_shop)
        setSupportActionBar(toolbar)

        ShipShop()

        try {
            titleToolbar.text = getString(R.string.detail_shop)
            val i = intent.getStringExtra("idOrder")
            val key = Constant.database.getReference(Constant.TB_SHOP)
            val ref = Constant.database.reference
            key.orderByChild("idOrder").equalTo(i)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children) {
                            detail = data.getValue(ItemPost::class.java)
                            harga = detail?.harga
//                            setToView(detail)
                            val uid = detail?.uid

                            ref.child(Constant.TB_MITRA).orderByChild("uid").equalTo(uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        for (issues in p0.children) {
                                            mitraData = issues.getValue(ItemMitra::class.java)
                                            setToView(detail, mitraData?.nama_mitra, mitraData?.foto, mitraData)
                                        }
                                    }

                                })
                        }
                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }

        ambilPenawaran.onClick {
            showQtyAndAddress(detail, mitraData, harga)
        }

    }

    private fun showQtyAndAddress(
        item: ItemPost?,
        data: ItemMitra?,
        harga: Int?
    ) {
        val dialog = AlertDialog.Builder(this@DetailMiShopActivity)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog, null)
        dialog.setView(view)
        dialog.setTitle("Konfirmasi Jumlah")

        val qty = view.findViewById<EditText>(R.id.qty)
        val alamat = view.findViewById<EditText>(R.id.alamat)
        val ongkir = view.findViewById<TextView>(R.id.ongkir)
        val hargaTv = view.findViewById<TextView>(R.id.harga)
        val total = view.findViewById<TextView>(R.id.total)
        val max = item?.maxPesanan
        qty.hint = "Maksimal Pesanan $max"
        qty.filters = max?.let { InputFilterMinMax(1, it) }?.let { arrayOf<InputFilter>(it) }
        ongkir.text = "Rp. " + ChangeFormat.toRupiahFormat2(tarif.toString())
        hargaTv.text = "Rp. " + ChangeFormat.toRupiahFormat2(harga.toString())
//        qty.textChangedListener {
//
//        }
        qty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    val kuantiti = ChangeFormat.clearRp(qty.text.toString()).toInt()
                    val ongkos = ChangeFormat.clearRp(ongkir.text.toString()).toInt()
                    val totl = harga?.let { kuantiti.times(it) }?.plus(ongkos)
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
        ) { _, _ -> orderShop(qty.text.toString().toInt(), item, alamat.text.toString(), data, harga) }

        dialog.setCancelable(true)
        dialog.show()

    }

    private fun orderShop(
        qty: Int,
        detail: ItemPost?,
        alamat: String,
        mitraData: ItemMitra?,
        harga: Int?
    ) {
        val shopOrderModel = ShopOrderModel()
        val time = HourToMillis.millis()
        shopOrderModel.idOrder = idShop.text.toString()
        shopOrderModel.id_order_shop = time.toString()
        shopOrderModel.email = Prefs.getString(Constant.EMAIL, "")
        shopOrderModel.uid = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.qty = qty
        shopOrderModel.ship_shop = tarif
        shopOrderModel.price_shop = detail?.kenaikan?.let { detail.harga?.plus(it) }
        shopOrderModel.lat_cust = Prefs.getDouble("lat", Constant.LAT_DEFAULT)
        shopOrderModel.lon_cust = Prefs.getDouble("lon", Constant.LON_DEFAULT)
        shopOrderModel.status_order_shop = 0
        shopOrderModel.uid = detail?.uid
        shopOrderModel.uidCustomer = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.tanggalOrder = time

        val notif = FirebaseMessagingModel()
        val model = FirebaseBooking()

        model.title = "Mi Shop Order"
        model.deskripsi = "Orderan Mi Shop ke $alamat"
        model.book = shopOrderModel
        model.type = 0

        notif.token = mitraData?.regid
        notif.data = model

        NetworkModule.getServiceFcm().actionSendMessage(notif)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe()


        val ref = Constant.database.getReference(Constant.TB_SHOP_ORDER)
        ref.child(time.toString()).setValue(shopOrderModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    alert {
                        message = "Orderan Berhasil Dimasukan. Silahkan Lihat Halam Proses untuk melihat Pesanan Anda"
                        title = "Berhasil"
                        okButton {
                            finish()
                            startActivity(intentFor<DashboardActivity>("notif" to 1).clearTop().clearTask().newTask())
                        }
                    }.show()
                } else {

                }
            }
            .addOnFailureListener {

            }

    }

    private fun ShipShop() {
        Constant.database.getReference(Constant.TB_TARIF)
            .orderByChild("tipe").equalTo("shop")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        val data = issue.getValue(Tarif::class.java)
                        tarif = data?.tarif?.toInt()
                    }
                }
            })
    }

    private fun setToView(
        detail: ItemPost?,
        nama_mitra: String?,
        foto: String?,
        mitraData: ItemMitra?
    ) {
        try {
            namaPosting.text = nama_mitra
            idShop.text = detail?.idOrder
            datePosting.text = detail?.tanggalPost?.toLong()?.let { HourToMillis.millisToDate(it) }
            lokasi.text = detail?.lokasi
            val harga = detail?.kenaikan?.let { detail.ongkos?.let { it1 -> detail.harga?.plus(it)?.plus(it1) } }
            hargaPost.text = "Rp. " + ChangeFormat.toRupiahFormat2(harga.toString())
            deskripsi.text = detail?.deskripsi
            Glide.with(this@DetailMiShopActivity)
                .load(detail?.foto)
                .into(imagePost)
            Glide.with(this@DetailMiShopActivity)
                .load(foto)
                .apply(RequestOptions.circleCropTransform())
                .into(fotouser)

            content.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}