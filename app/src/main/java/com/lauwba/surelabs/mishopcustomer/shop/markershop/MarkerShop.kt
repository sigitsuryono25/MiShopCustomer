@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.shop.markershop

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.lauwba.surelabs.mishopcustomer.shop.model.ShopOrderModel
import kotlinx.android.synthetic.main.activity_marker_shop.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MarkerShop : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var lat: MutableList<Double>? = null
    private var lon: MutableList<Double>? = null
    private var title: MutableList<String>? = null
    private var progressDialog: ProgressDialog? = null
    private var latLngBounds: LatLngBounds? = null
    private var latLngBoundsBuilder: LatLngBounds.Builder? = null
    private var idOrder: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_shop)

        try {
            idOrder = intent.getStringExtra("idOrder")
            lat = mutableListOf()
            lon = mutableListOf()
            title = mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        showAll.onClick { getDataOrder() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings?.isZoomControlsEnabled = true
        try {
            getDataOrder()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDataOrder() {
        progressDialog = ProgressDialog.show(this@MarkerShop, "", "Memuat halaman", false, true)
        try {
            val ref = Constant.database.getReference(Constant.TB_SHOP_ORDER)
            val user = Constant.database.getReference(Constant.TB_CUSTOMER)
            ref.orderByChild("idOrder").equalTo(idOrder)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        progressDialog?.dismiss()
                        try {
                            for (issue in p0.children) {
                                val data = issue.getValue(ShopOrderModel::class.java)
                                val uidCustomer = data?.uidCustomer

                                user.orderByChild("uid").equalTo(uidCustomer)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            for (issues in p0.children) {
                                                val customer = issues.getValue(Customer::class.java)
                                                customer?.nama?.let { title?.add(it) }
                                                data?.lat_cust?.let { lat?.add(it) }
                                                data?.lon_cust?.let { lon?.add(it) }
                                                lat?.let {
                                                    lon?.let { it1 ->
                                                        title?.let { it2 ->
                                                            initMarker(
                                                                it,
                                                                it1,
                                                                it2
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    })
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initMarker(lat: MutableList<Double>, lon: MutableList<Double>, title: MutableList<String>) {
        latLngBoundsBuilder = LatLngBounds.Builder()
        for (i in 0 until lat.size) {
            drawMarker(LatLng(lat[i], lon[i]), title[i])
        }
        latLngBounds = latLngBoundsBuilder!!.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen

        val cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, height, padding)
        mMap.animateCamera(cu)
    }

    private fun drawMarker(latLng: LatLng, title: String) {
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin2))
        mMap.addMarker(markerOptions).showInfoWindow()
        latLngBoundsBuilder?.include(markerOptions.position)
    }
}
