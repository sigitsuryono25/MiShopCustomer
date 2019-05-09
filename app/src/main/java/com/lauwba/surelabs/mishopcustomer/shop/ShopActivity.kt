package com.lauwba.surelabs.mishopcustomer.shop

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model.Distance
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.services.MyLocationService
import com.lauwba.surelabs.mishopcustomer.shop.adapter.TimeLineAdapter
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_shop.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startService

class ShopActivity : AppCompatActivity() {
    private var mList: MutableList<ItemPost>? = null
    private var tarif: Int? = null
    private var lastTanggal: String? = null
    private var dis: CompositeDisposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        setSupportActionBar(toolbar)
        titleToolbar.text = "Mi Shop"
        Glide.with(this@ShopActivity).load(R.drawable.new_shop)
            .into(iconLayanan)
        mList = mutableListOf()

        startService<MyLocationService>()
        getTarif()
    }

    private fun getTarif() {
        val ref = Constant.database.getReference(Constant.TB_TARIF)
        ref.orderByChild("tipe").equalTo("shop")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        val data = issue.getValue(Tarif::class.java)
                        tarif = data?.tarif?.toInt()
                        getDataShop()
                    }
                }
            })
    }

    private fun getDataShop() {
        try {
            val ref = Constant.database.getReference(Constant.TB_SHOP)
            ref.orderByChild("tanggalPost").limitToFirst(10)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }


                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(ItemPost::class.java)
                                if (data?.statusPost?.equals(
                                        "expired",
                                        true
                                    ) == false || data?.statusPost.isNullOrEmpty()
                                ) {
                                    checkDistance(
                                        Prefs.getDouble("lat", Constant.LAT_DEFAULT),
                                        Prefs.getDouble("lon", Constant.LON_DEFAULT),
                                        data?.lat,
                                        data?.lon,
                                        data
                                    )
                                }
                            }
                        } else {

                        }
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkDistance(
        latAwal: Double?,
        lonAwal: Double?,
        latTujuan: Double?,
        lonTujuan: Double?,
        post: ItemPost?
    ) {
        dis = CompositeDisposable()
        val origin = "$latAwal, $lonAwal"
        val destination = "$latTujuan, $lonTujuan"

        dis?.add(
            NetworkModule.getService().actionRoute(
                origin,
                destination,
                getString(R.string.google_maps_key) ?: ""
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val route = it?.routes?.get(0)
                    val overview = route?.overviewPolyline
                    val point = overview?.points
                    val distance = route?.legs?.get(0)?.distance

                    hitungJarak(distance, post)
                }, {

                })
        )
    }

    private fun hitungJarak(
        distance: Distance?,
        post: ItemPost?
    ) {
        val jarak = distance?.value

        val valueBagi = jarak?.div(1000)
        val valueBulat = Math.ceil(valueBagi?.toDouble() ?: 0.0)
        Log.d("JARAK", valueBulat.toString())

        if (valueBulat <= Constant.JARAK_MAKSIMAL) {
            post?.let { mList?.add(it) }
            setItemToAdapter(mList, tarif)
        }

    }

    private fun setItemToAdapter(
        mList: MutableList<ItemPost>?,
        tarif: Int?
    ) {
        mList?.sortByDescending {
            it.tanggalPost
        }


        val adapter = TimeLineAdapter(mList, this, tarif)
        try {
            timeline.layoutManager = LinearLayoutManager(this)
            timeline.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
