package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress

import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking.CarBikeBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.firebase.FirebaseBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.firebase.NotificationBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model.Distance
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.waiting.WaitingActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.libs.DirectionMapsV2
import com.lauwba.surelabs.mishopcustomer.libs.GPSTracker
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mi_things.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import java.util.*

class MiCarActivity : AppCompatActivity(), OnMapReadyCallback {

    var mMap: GoogleMap? = null
    var latAwal: Double? = null
    var lonAwal: Double? = null
    var latTujuan: Double? = null
    var lonTujuan: Double? = null
    var gps: GPSTracker? = null
    var dis: CompositeDisposable? = null
    var harga : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_things)

        checkPermissionGps()
        initView()

        val mp = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mp.onCreate(savedInstanceState)
        mp.getMapAsync(this)
    }

    private fun initView() {
        asal.onClick {
            showPlace(1)
        }

        tujuan.onClick {
            showPlace(2)
        }

        booking.onClick {
            insertFirebase()
        }
    }

    private fun insertFirebase() {
        val myref = Config.databaseInstance(Constant.TB_CAR_ORDER)

        val booking = CarBikeBooking()
        val time = Calendar.getInstance()
        val idOrder = time.timeInMillis

        booking.latAwal = latAwal
        booking.lonAwal = lonAwal
        booking.latTujuan = latTujuan
        booking.lonTujuan = lonTujuan
        booking.jarak = jarakTrip.text.toString()
        booking.tanggal = idOrder
        booking.idOrder= idOrder.toString()
        booking.harga = harga
        booking.status = 1
        booking.lokasiAwal = asal.text.toString()
        booking.lokasiTujuan = tujuan.text.toString()
        booking.uid = Config.authInstanceCurrentUser()

        myref.child(idOrder.toString()).setValue(booking).addOnCompleteListener {

            if (it.isSuccessful) {
                val notificationBooking = NotificationBooking()
                val firebaseBooking = FirebaseBooking()

                firebaseBooking.title = "Orderan Mi-Car"
                firebaseBooking.deskripsi = asal.text.toString() + " - " + tujuan.text.toString()
                firebaseBooking.book = booking
                firebaseBooking.type = 1

                notificationBooking.token = "/topics/micar"
                notificationBooking.booking = firebaseBooking

                NetworkModule.getServiceFcm()
                    .actionSendBook(notificationBooking)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()


                startActivity<WaitingActivity>("key" to idOrder.toString())
            }
        }
    }

    private fun showPlace(status: Int) {
        val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
            .build(this)
        startActivityForResult(intent, status)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {

            val place = PlaceAutocomplete.getPlace(this, data)

            if (requestCode == 1) {
                latAwal = place.latLng.latitude
                lonAwal = place.latLng.longitude

                if (tujuan.text.length > 0) {
                    mMap?.clear()

                    showMarker(latTujuan, lonTujuan, place.name.toString())
                    showMarker(latAwal, lonAwal, place.name.toString())
                    showBound()
                    route()
                }

                val name = place.address
                asal.text = name
            } else if (requestCode == 2) {
                if (asal.text.length > 0) {
                    mMap?.clear()

                    showMarker(latAwal, lonAwal, place.name.toString())
                }

                latTujuan = place.latLng.latitude
                lonTujuan = place.latLng.longitude

                var name = place.address
                tujuan.text = name

                showMarker(latTujuan, lonTujuan, place.name.toString())
                showBound()
                route()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun route() {
        dis = CompositeDisposable()
        val origin = "$latAwal, $lonAwal"
        val destination = "$latTujuan, $lonTujuan"

        dis?.add(
            NetworkModule.getService().actionRoute(
                origin, destination,
                this.getString(R.string.google_maps_key) ?: ""
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val route = it?.routes?.get(0)
                    val overview = route?.overviewPolyline
                    val point = overview?.points
                    val distance = route?.legs?.get(0)?.distance

                    showHarga(distance)

                    mMap?.let { it1 -> point?.let { it2 -> DirectionMapsV2.gambarRoute(it1, it2) } }
                }, {

                })
        )


    }

    private fun showHarga(distance: Distance?) {
        val text = distance?.text
        val value = distance?.value

        val valueBagi = value?.div(1000)
        val valueBulat = Math.ceil(valueBagi?.toDouble() ?: 0.0)

        var hargaAwal = 0.0
//        if (valueBulat < 5) {
        hargaAwal = valueBulat * 4200
        harga = hargaAwal.toInt()
//        } else {
//            hargaAwal = ((valueBulat - 5) * 1000) + (5 * 2000)
//        }

        var resultHarga = ChangeFormat.toRupiahFormat2("$hargaAwal")

        jarakTrip.text = text
        hargaTrip.text = "Rp. " + resultHarga

        if (hargaTrip.text.length > 0) {
            booking.background = resources.getDrawable(R.color.com_facebook_button_background_color_pressed)
            booking.isEnabled = true
        }
    }

    private fun showBound() {
        if (latAwal != 0.0 && lonAwal != 0.0 && latTujuan != 0.0 && lonTujuan != 0.0)
            bottom.visibility = View.VISIBLE
        mMap?.setPadding(200, 200, 200, 200)
        val bound = LatLngBounds.builder()
        bound.include(LatLng(latAwal ?: 0.0, lonAwal ?: 0.0))
        bound.include(LatLng(latTujuan ?: 0.0, lonTujuan ?: 0.0))

        mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 15))
    }

    private fun checkPermissionGps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        } else {
            try {
                showGps()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            try {
                showGps()
            } catch (e: Exception) {

            }
        }
    }

    private fun showGps() {
        gps = GPSTracker(this)
        if (gps?.canGetLocation() != false) {
            latAwal = gps?.latitude
            lonAwal = gps?.longitude

            val name = showNameLocations(latAwal, lonAwal)

            asal.text = name

            showMarker(latAwal, lonAwal, "My Location")

        }
    }

    private fun showMarker(lat: Double?, lon: Double?, title: String?) {
        val posisi = LatLng(lat ?: 0.0, lon ?: 0.0)
        mMap?.addMarker(MarkerOptions().position(posisi).title(title))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(posisi, 15f))
    }

    private fun showNameLocations(lat: Double?, lon: Double?): CharSequence? {
        val geocoder = Geocoder(this, Locale.getDefault())
        val location = geocoder.getFromLocation(lat ?: 0.0, lon ?: 0.0, 1)

        //get address location
        val nameLocations = location.get(0).getAddressLine(0)

        return nameLocations
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
    }
}
