package com.lauwba.surelabs.mishopcustomer.myShop.detail

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.myShop.model.MyShopModel
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.activity_detail_my_shop.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast

class DetailMyShopActivity : AppCompatActivity() {

    private var idMyShop: String? = null
    private var pd : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_my_shop)
        setSupportActionBar(toolbar)
        titleToolbar.text = "Detail Post My Shop"

        try {
            idMyShop = intent.getStringExtra("idMyShop")
            getDetailMyShop(idMyShop)
            toast(idMyShop ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDetailMyShop(idMyShop: String?) {
        pd = ProgressDialog.show(this@DetailMyShopActivity, "", "Memuat halaman...", false, true)
        val ref = Constant.database.getReference(Constant.TB_MYSHOP)
        val user = Constant.database.getReference(Constant.TB_CUSTOMER)
        ref.orderByChild("idMyShop").equalTo(idMyShop)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {


                }

                override fun onDataChange(p0: DataSnapshot) {
                    pd?.dismiss()
                    for (issue in p0.children) {
                        val data = issue.getValue(MyShopModel::class.java)
                        val uid = data?.uid

                        user.orderByChild("uid").equalTo(uid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    for (issues in p0.children) {
                                        val content = issues.getValue(Customer::class.java)
                                        setToView(data, content)
                                    }
                                }
                            })

                    }
                }
            })
    }

    private fun setToView(
        data: MyShopModel?,
        content: Customer?
    ) {
        try {
            idShop.text = data?.idMyShop.toString()
            Glide.with(this@DetailMyShopActivity)
                .load(content?.fotoCustomer)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(fotouser)
            datePosting.text = data?.tanggalPost?.let { HourToMillis.millisToDate(it) }
            namaPosting.text = data?.judul
            lokasi.text = data?.lokasi
            hargaPost.text = "Rp. " + ChangeFormat.toRupiahFormat2(data?.harga.toString())
            deskripsi.text = data?.deskripsi
            Glide.with(this@DetailMyShopActivity)
                .load(data?.image)
                .into(imagePost)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
