package com.lauwba.surelabs.mishopcustomer.shop

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.shop.adapter.TimeLineAdapter
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import kotlinx.android.synthetic.main.activity_shop.*

class ShopActivity : AppCompatActivity() {

    val namaPosting = arrayOf("Sigit Suryono", "Rafi Aqil Al Dzikri")
    val datePosting = arrayOf("25 Februari 2019", "03 Juni 2003")
    val hargaPost = arrayOf("Rp. 22.500", "Rp. 12.500")
    val lokasi = arrayOf("Ayam Panggang 3 Berku", "Seblak Muantep")
    val fotouser = arrayOf(
        "https://pbs.twimg.com/profile_images/1071222936021790721/kJAz840E_400x400.jpg",
        "https://scontent.fsoc1-1.fna.fbcdn.net/v/t1.0-9/41423540_2190550337889157_2425118249638166528_n.jpg?_nc_cat=108&_nc_ht=scontent.fsoc1-1.fna&oh=994bd71a806c32bda32a6d3844e11bb4&oe=5CF78E7D"
    )

    val imagePost = arrayOf(
        "http://kuliner.panduanwisata.id/files/2012/08/ayam-utuh.jpg",
        "https://cdn.idntimes.com/content-images/community/2018/04/w2blgd6kao1481008955-88d73c4dbd5a60ffc2c5e3ad34c47e07_600x400.jpg"
    )
    val idshop = arrayOf("25", "03")

    var item: ItemPost? = null
    var mList: MutableList<ItemPost>? = null
    var adapter: TimeLineAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        val deskripsi = arrayOf(resources.getString(R.string.lorem), resources.getString(R.string.lorem))

        mList = mutableListOf()
//        for(i in 0 until namaPosting.size){
//            item = ItemPost(namaPosting[i], datePosting[i], hargaPost[i], fotouser[i], deskripsi[i], imagePost[i], idshop[i], lokasi[i])
//            mList?.add(item!!)
//        }

        adapter = TimeLineAdapter(mList, this)
        timeline.layoutManager = LinearLayoutManager(this)
        timeline.adapter = adapter
    }
}
