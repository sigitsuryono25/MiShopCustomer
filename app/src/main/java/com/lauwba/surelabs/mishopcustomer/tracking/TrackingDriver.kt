package com.lauwba.surelabs.mishopcustomer.tracking

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking.CarBikeBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiCarActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import kotlinx.android.synthetic.main.activity_tracking_driver.*
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sendSMS
import org.jetbrains.anko.startActivity

class TrackingDriver : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var book: CarBikeBooking? = null
    private var phone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_driver)


        book = intent.getSerializableExtra("booking") as CarBikeBooking

        homeprice.text = "Rp. " + ChangeFormat.toRupiahFormat2(book?.harga.toString())
        homeAwal.text = book?.lokasiAwal
        homeTujuan.text = book?.lokasiTujuan
        homeWaktudistance.text = book?.jarak


        val fragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        fragment.getMapAsync(this)

        homebuttonnext.onClick {
            startActivity<MiCarActivity>()
        }

        call.onClick {
            phone?.let { it1 -> makeCall(it1) }
        }

        sms.onClick {
            phone?.let { it1 -> sendSMS(it1) }
        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0

        val myRef = Constant.database.getReference(Constant.TB_MITRA)

        val query = myRef.orderByChild("uid").equalTo(book?.driver)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (issue in p0.children) {
                    val driver = issue.getValue(ItemMitra::class.java)
                    phone = driver?.no_tel
//                    if(book?.type == 2) {
                    showData(driver, BitmapDescriptorFactory.fromResource(R.drawable.driver_icon4))
//                    }else{
//                        showData(driver, BitmapDescriptorFactory.fromResource(R.drawable.car))
//                    }
                }
            }

        })
    }

    private fun showData(driver: ItemMitra?, icon: BitmapDescriptor?) {
        mMap?.clear()
        val coordinate = LatLng(driver?.lat ?: 0.0, driver?.lon ?: 0.0)
        val destinasi = LatLng(book?.latTujuan ?: 0.0, book?.lonTujuan ?: 0.0)
        val myLoc = LatLng(book?.latAwal ?: 0.0, book?.lonAwal ?: 0.0)
        mMap?.addMarker(MarkerOptions().position(coordinate).title(driver?.nama_mitra).icon(icon))?.showInfoWindow()
        mMap?.addMarker(
            MarkerOptions().position(destinasi).title(book?.lokasiTujuan).icon(
                BitmapDescriptorFactory.fromResource(
                    R.drawable.ic_pin1
                )
            )
        )?.showInfoWindow()
        mMap?.addMarker(
            MarkerOptions().position(myLoc).title(book?.lokasiAwal).icon(
                BitmapDescriptorFactory.fromResource(
                    R.drawable.ic_pin2
                )
            )
        )?.showInfoWindow()
//        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12f))

        mMap?.setOnInfoWindowClickListener {
            //            startActivity()
        }

        mMap?.setPadding(200, 200, 200, 200)
        val bound = LatLngBounds.builder()
        bound.include(destinasi)
        bound.include(myLoc)
        bound.include(coordinate)

        mMap?.setOnMapLoadedCallback {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 12))
        }

        Glide.with(this@TrackingDriver)
            .load(driver?.foto)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(driverImage)

        driverName.text = driver?.nama_mitra
        plat.text = "AB 6729 POQ"
    }

}
