package com.lauwba.surelabs.mishopcustomer.shop.detail

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.service.model.ServiceOrderModel
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.lauwba.surelabs.mishopcustomer.shop.model.ShopOrderModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_detail_mi_shop.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sendSMS

class DetailMiShopActivity : AppCompatActivity() {
    private var tarif: Int? = null
    private var detail: ServiceOrderModel.MiService? = null
    private var shop: ItemPost? = null
    private var from: String? = null
    private var mitra: ItemMitra? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mi_shop)
        setSupportActionBar(toolbar)



        try {
            titleToolbar.text = getString(R.string.detail_shop)
            from = intent.getStringExtra("from")
            val i = intent.getStringExtra("idOrder")
            when (from) {
                Constant.TB_SHOP -> {
                    getDetailShop(i)
                }

                Constant.TB_SERVICE -> {
                    getDetailService(i)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        ambilPenawaran.onClick {
            if (from?.equals(Constant.TB_SHOP) == true)
                showQtyAndAddress()
            else
                showAlert(mitra)
        }

    }

    private fun showAlert(mitra: ItemMitra?) {
        val layout = LayoutInflater.from(applicationContext)
        val nomorTelpon = mitra?.no_tel
        val v = layout.inflate(R.layout.layout_mitra, null)
        val alertDialog = AlertDialog.Builder(this@DetailMiShopActivity)
        alertDialog.setView(v)
        val foto = v.findViewById<ImageView>(R.id.fotomitra)
        val nama = v.findViewById<TextView>(R.id.namaMitra)
        val tel = v.findViewById<TextView>(R.id.nomorTelepon)
        val wa = v.findViewById<TextView>(R.id.wa)
        val sms = v.findViewById<TextView>(R.id.sms)
        val telepon = v.findViewById<TextView>(R.id.call)
        nama.text = mitra?.nama_mitra
        tel.text = nomorTelpon
        Glide.with(applicationContext)
            .load(mitra?.foto)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(foto)
        wa.onClick {
            val url = "whatsapp://send?phone=$nomorTelpon"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        sms.onClick {
            nomorTelpon?.let { it1 -> sendSMS(it1) }
        }

        telepon.onClick {
            nomorTelpon?.let { it1 -> makeCall(it1) }
        }

        alertDialog.create().show()
    }

    private fun getDetailService(i: String) {
        val key = Constant.database.getReference(Constant.TB_SERVICE)
        val ref = Constant.database.reference
        key.orderByChild("idOrder").equalTo(i)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (data in p0.children) {
                        detail = data.getValue(ServiceOrderModel.MiService::class.java)
//                            setToView(detail)
                        val uid = detail?.uid

                        ref.child(Constant.TB_MITRA).orderByChild("uid").equalTo(uid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    for (issues in p0.children) {
                                        mitra = issues.getValue(ItemMitra::class.java)
                                        setToViewServices(detail, mitra?.nama_mitra)
                                    }
                                }

                            })
                    }
                }

            })
    }

    private fun getDetailShop(i: String) {
        val key = Constant.database.getReference(Constant.TB_SHOP)
        val ref = Constant.database.reference
        key.orderByChild("idOrder").equalTo(i)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (data in p0.children) {
                        shop = data.getValue(ItemPost::class.java)
//                            setToView(detail)
                        val uid = (detail as ItemPost?)?.uid

                        ref.child(Constant.TB_MITRA).orderByChild("uid").equalTo(uid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    for (issues in p0.children) {
                                        val mitraData = issues.getValue(ItemMitra::class.java)
                                        setToView(detail as ItemPost?, mitraData?.nama_mitra, mitraData?.foto)
                                    }
                                }

                            })
                    }
                }

            })
    }

    private fun showQtyAndAddress() {
        val dialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog, null)
        dialog.setView(view)
        dialog.setCancelable(true)
        dialog.setTitle("Konfirmasi Jumlah")

        val qty = view.findViewById<EditText>(R.id.qty)
        val alamat = view.findViewById<EditText>(R.id.alamat)
        alamat.setText(Prefs.getString(Constant.ALAMAT, ""))

        dialog.setPositiveButton("Order", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                orderShop(qty.text.toString().toInt(), detail as ItemPost?)
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
        shopOrderModel.ship_shop = tarif
        shopOrderModel.price_shop = detail?.kenaikan?.let { detail.harga?.plus(it) }
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

    private fun ShipServices() {
        Constant.database.getReference(Constant.TB_TARIF)
            .orderByChild("tipe").equalTo("service")
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
        foto: String?
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
            ShipShop()
            content.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setToViewServices(
        detail: ServiceOrderModel.MiService?,
        nama_mitra: String?
    ) {
        try {
            namaPosting.text = nama_mitra
            idShop.text = detail?.idOrder
            datePosting.text = detail?.tanggal?.let { HourToMillis.millisToDate(it) }
            lokasi.text = detail?.namaService
            val harga = detail?.ship_service?.let { it1 -> detail.harga?.plus(it1) }
            hargaPost.text = "Rp. " + ChangeFormat.toRupiahFormat2(harga.toString())
            deskripsi.text = detail?.deskripsi
            Glide.with(this@DetailMiShopActivity)
                .load(detail?.foto)
                .into(imagePost)
            ShipServices()
            content.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
