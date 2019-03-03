package com.lauwba.surelabs.mishopcustomer.shop.detail

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.lauwba.surelabs.mishopcustomer.shop.model.ShopOrderModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_detail_mi_shop.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick

class DetailMiShopActivity : AppCompatActivity() {
    private var getTarif = Tarif()
    private var detail: ItemPost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mi_shop)
        setSupportActionBar(toolbar)

        try {
            titleToolbar.text = getString(R.string.detail_shop)
            val i = intent.getStringExtra("idOrder")
            val key = Config.database.getReference(Constant.TB_SHOP)
            val ref = Constant.database.reference
            key.orderByChild("idOrder").equalTo(i)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children) {
                            detail = data.getValue(ItemPost::class.java)
//                            setToView(detail)
                            val uid = detail?.uid

                            ref.child(Constant.TB_MITRA).orderByChild("uid").equalTo(uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        for (issues in p0.children) {
                                            val mitraData = issues.getValue(ItemMitra::class.java)
                                            setToView(detail, mitraData?.nama_mitra, mitraData?.foto)
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
            showQtyAndAddress()
        }

    }

    private fun showQtyAndAddress() {
        val dialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog, null)
        dialog.setView(view)
        dialog.setCancelable(false)
        dialog.setTitle("Konfirmasi Jumlah")

        val qty = view.findViewById<EditText>(R.id.qty)
        val alamat = view.findViewById<EditText>(R.id.alamat)
        alamat.setText(Prefs.getString(Constant.ALAMAT, ""))

        dialog.setPositiveButton("Order", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                orderShop(qty.text.toString().toInt(), detail)
            }

        })

        dialog.show()

    }

    private fun orderShop(
        qty: Int,
        detail: ItemPost?
    ) {
        val shopOrderModel = ShopOrderModel()
        val time = HourToMillis.millis()
        shopOrderModel.idOrder = idShop.text.toString()
        shopOrderModel.id_order_shop = time.toString()
        shopOrderModel.email = Prefs.getString(Constant.EMAIL, "")
        shopOrderModel.uid = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.qty = qty
        shopOrderModel.ship_shop = getTarif.getTarif("add")
        shopOrderModel.price_shop = detail?.harga
        shopOrderModel.lat_cust = 0.0
        shopOrderModel.lon_cust = 0.0
        shopOrderModel.status_order_shop = 0
        shopOrderModel.uid = detail?.uid
        shopOrderModel.uidCustomer = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        shopOrderModel.tanggalOrder = time


        val ref = Constant.database.getReference(Constant.TB_SHOP_ORDER)
        ref.child(time.toString()).setValue(shopOrderModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    alert {
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

    private fun setToView(
        detail: ItemPost?,
        nama_mitra: String?,
        foto: String?
    ) {
        try {
            namaPosting.text = nama_mitra
            idShop.text = detail?.idOrder
            datePosting.text = detail?.tanggalPost?.toLong()?.let { HourToMillis.millisToDate(it) }
            lokasi.text = detail?.lokasi
            hargaPost.text = "Rp. " + ChangeFormat.toRupiahFormat2(detail?.harga.toString())
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
