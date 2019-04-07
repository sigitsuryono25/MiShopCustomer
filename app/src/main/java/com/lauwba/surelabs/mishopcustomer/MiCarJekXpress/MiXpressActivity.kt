package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress

import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.iid.FirebaseInstanceId
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
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mi_things.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class MiXpressActivity : AppCompatActivity(), OnMapReadyCallback {

    var mMap: GoogleMap? = null
    var latAwal: Double? = null
    var lonAwal: Double? = null
    var latTujuan: Double? = null
    var lonTujuan: Double? = null
    var gps: GPSTracker? = null
    var dis: CompositeDisposable? = null
    var harga: Int? = null
    var idOrder: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_things)

        checkPermissionGps()
        initView()

        try {
            idOrder = intent.getStringExtra("idOrder")
            Log.d("idOrder", idOrder)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
            if (!idOrder.isNullOrEmpty()) {
                updateOrderan()
            } else {
                insertFirebase()
            }
        }
    }

    private fun updateOrderan() {
        val ref = Constant.database.getReference(Constant.TB_EXPRESS)
        ref.child(idOrder ?: "").child("harga").setValue(harga)
        ref.child(idOrder ?: "").child("jarak").setValue(jarakTrip.text.toString())
        ref.child(idOrder ?: "").child("latAwal").setValue(latAwal)
        ref.child(idOrder ?: "").child("lonAwal").setValue(lonAwal)
        ref.child(idOrder ?: "").child("lokasiAwal").setValue(asal.text.toString())
        ref.child(idOrder ?: "").child("lokasiTujuan").setValue(tujuan.text.toString())
        ref.child(idOrder ?: "").child("latTujuan").setValue(latTujuan)
        ref.child(idOrder ?: "").child("lonTujuan").setValue(lonTujuan)
        ref.child(idOrder ?: "").child("namaBarang").setValue(barang.text.toString())
        ref.child(idOrder ?: "").child("nomorYangDihubungi").setValue(nomorTelepon.text.toString())
        ref.child(idOrder ?: "").child("status").setValue(1)
        ref.child(idOrder ?: "").child("uid").setValue(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))

        val i = Intent(this@MiXpressActivity, WaitingActivity::class.java)
        i.putExtra("key", idOrder.toString())
        i.putExtra("from", Constant.TB_EXPRESS)
        startActivity(i)
    }

    private fun insertFirebase() {
        val myref = Config.databaseInstance(Constant.TB_EXPRESS)

        val booking = CarBikeBooking()
        val time = Calendar.getInstance()
        val idOrder = time.timeInMillis

        booking.namaCustomer = Prefs.getString(Constant.NAMA_CUSTOMER, "")
        booking.token = Prefs.getString(Constant.TOKEN, FirebaseInstanceId.getInstance().token)
        booking.nomorTelepon = Prefs.getString(Constant.TELEPON, "")
        booking.foto = Prefs.getString(Constant.FOTO, "")

        booking.tanggal = idOrder
        booking.idOrder = idOrder.toString()
        booking.uidCustomer = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        booking.lokasiAwal = asal.text.toString()
        booking.latAwal = latAwal
        booking.lonAwal = lonAwal
        booking.lokasiTujuan = tujuan.text.toString()
        booking.latTujuan = latTujuan
        booking.lonTujuan = lonTujuan
        booking.harga = harga
        booking.jarak = jarakTrip.text.toString()
        booking.status = 5
        booking.type = 3
        booking.namaBarang = barang.text.toString()
        booking.nomorYangDihubungi = nomorTelepon.text.toString()
        booking.driver = ""
        myref.child(idOrder.toString()).setValue(booking).addOnCompleteListener {

            if (it.isSuccessful) {
                val notificationBooking = NotificationBooking()
                val firebaseBooking = FirebaseBooking()

                firebaseBooking.title = "Orderan Mi-Express"
                firebaseBooking.deskripsi = asal.text.toString() + " - " + tujuan.text.toString()
                firebaseBooking.book = booking
                firebaseBooking.type = 3

                notificationBooking.token = "/topics/miexpress"
                notificationBooking.booking = firebaseBooking

                NetworkModule.getServiceFcm()
                    .actionSendBook(notificationBooking)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                val i = Intent(this@MiXpressActivity, WaitingActivity::class.java)
                i.putExtra("key", idOrder.toString())
                i.putExtra("from", Constant.TB_EXPRESS)
                startActivity(i)
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

                    showMarker(
                        latTujuan,
                        lonTujuan,
                        place.name.toString(),
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_pin1)
                    )
                    showMarker(
                        latAwal,
                        lonAwal,
                        place.name.toString(),
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_pin2)
                    )
                    showBound()
                    route()
                }

                val name = place.address
                asal.text = name
            } else if (requestCode == 2) {
                if (asal.text.length > 0) {
                    mMap?.clear()

                    showMarker(
                        latAwal,
                        lonAwal,
                        place.name.toString(),
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_pin1)
                    )
                }

                latTujuan = place.latLng.latitude
                lonTujuan = place.latLng.longitude

                var name = place.address
                tujuan.text = name

                showMarker(
                    latTujuan,
                    lonTujuan,
                    place.name.toString(),
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_pin2)
                )
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
        hargaAwal = valueBulat * 1750
        harga = hargaAwal.toInt()

        val resultHarga = ChangeFormat.toRupiahFormat2("$hargaAwal")

        jarakTrip.text = text
        hargaTrip.text = "Rp. " + resultHarga

        if (hargaTrip.text.length > 0) {
            booking.background = resources.getDrawable(R.color.com_facebook_button_background_color_pressed)
            booking.isEnabled = true
            barangContainer.visibility = View.VISIBLE
            teleponContainer.visibility = View.VISIBLE
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

            showMarker(latAwal, lonAwal, "My Location", BitmapDescriptorFactory.fromResource(R.drawable.ic_pin1))

        }
    }

    private fun showMarker(lat: Double?, lon: Double?, title: String?, icon: BitmapDescriptor?) {
        val posisi = LatLng(lat ?: 0.0, lon ?: 0.0)
        mMap?.addMarker(MarkerOptions().position(posisi).title(title).icon(icon))
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