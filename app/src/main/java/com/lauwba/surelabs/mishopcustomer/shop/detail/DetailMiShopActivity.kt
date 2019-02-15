package com.lauwba.surelabs.mishopcustomer.shop.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import kotlinx.android.synthetic.main.activity_detail_mi_shop.*
import kotlinx.android.synthetic.main.toolbar.*

class DetailMiShopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mi_shop)

        try {
            titleToolbar.text = getString(R.string.detail_shop)
            val i = intent.getStringExtra("idOrder")
            val key = Config.database.getReference(Config.tb_shop)
            key.orderByChild("idOrder").equalTo(i)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children) {
                            val detail = data.getValue(ItemPost::class.java)
                            setToView(detail)
                        }
                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setToView(detail: ItemPost?) {
        idShop.text = detail?.idOrder
        datePosting.text = detail?.tanggalPost
        lokasi.text = detail?.lokasi
        hargaPost.text = detail?.harga
        deskripsi.text = detail?.deskripsi
        Glide.with(this@DetailMiShopActivity)
            .load("https://firebasestorage.googleapis.com/v0/b/mishop.appspot.com/o/images%2F1550223267719%2F1550223267719.jpg?alt=media&token=5bf62418-e80c-428f-8983-f01fc1ef94cf")
            .into(imagePost)
//        imagePost.setImageURI(Uri.parse(detail?.foto))
        Glide.with(this@DetailMiShopActivity)
            .load("https://scontent.fsoc1-1.fna.fbcdn.net/v/t1.0-9/41423540_2190550337889157_2425118249638166528_n.jpg?_nc_cat=108&_nc_ht=scontent.fsoc1-1.fna&oh=994bd71a806c32bda32a6d3844e11bb4&oe=5CF78E7D")
            .apply(RequestOptions.circleCropTransform())
            .into(fotouser)
    }
}
