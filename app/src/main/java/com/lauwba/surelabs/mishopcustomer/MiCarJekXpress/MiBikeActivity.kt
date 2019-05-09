@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress

import android.app.ProgressDialog
import android.content.Intent
import android.location.Geocoder
import android.os.AsyncTask
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking.CarBikeBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.firebase.FirebaseBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.firebase.NotificationBooking
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model.Distance
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.waiting.WaitingActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.chat.model.ItemChat
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.config.Tarif
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.libs.DirectionMapsV2
import com.lauwba.surelabs.mishopcustomer.libs.GPSTracker
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishoplatest.chat.model.FirebaseMessagingMessage
import com.lauwba.surelabs.mishoplatest.chat.model.NotificationMessage
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.new_micar_mibike.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import java.util.*

class MiBikeActivity : AppCompatActivity(), OnMapReadyCallback {

    private var pd: ProgressDialog? = null
    private var tarif: String? = null
    var mMap: GoogleMap? = null
    var latAwal: Double? = null
    var lonAwal: Double? = null
    var latTujuan: Double? = null
    var lonTujuan: Double? = null
    var gps: GPSTracker? = null
    var dis: CompositeDisposable? = null
    var harga: Int? = null
    var idOrder: String? = null
    var regid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_micar_mibike)

//        Glide.with(this@MiBikeActivity).load(R.drawable.new_bike)
//            .into(iconLayanan)

        checkPermissionGps()
        initView()

        try {
            idOrder = intent.getStringExtra("idOrder")

            getDetailMitra(intent.getStringExtra("driver"))
            Log.d("idOrder", idOrder)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val mp = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mp.onCreate(savedInstanceState)
        mp.getMapAsync(this)

        GetTarifMiBike().execute()
    }

    private fun getDetailMitra(uid: String?) {
        val ref = Constant.database.getReference(Constant.TB_MITRA)
        ref.orderByChild("uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issue in p0.children) {
                            val data = issue.getValue(ItemMitra::class.java)
                            regid = data?.regid
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    inner class GetTarifMiBike : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pd = ProgressDialog.show(this@MiBikeActivity, "", "Memuat Halaman...", false, false)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            pd?.dismiss()
            val ref = Constant.database.getReference(Constant.TB_TARIF)
            ref.orderByChild("tipe").equalTo("bike")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            for (issues in p0.children) {
                                val data = issues.getValue(Tarif::class.java)
                                tarif = data?.tarif
                                runOnUiThread {
                                    toast(tarif.toString())
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            return null
        }

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

        myLoc.onClick {
            checkPermissionGps()
        }
    }

    private fun updateOrderan() {
        val ref = Constant.database.getReference(Constant.TB_BIKE)
        ref.child(idOrder ?: "").child("harga").setValue(harga)
        ref.child(idOrder ?: "").child("jarak").setValue(jarakTrip.text.toString())
        ref.child(idOrder ?: "").child("latAwal").setValue(latAwal)
        ref.child(idOrder ?: "").child("lonAwal").setValue(lonAwal)
        ref.child(idOrder ?: "").child("lokasiAwal").setValue(asal.text.toString())
        ref.child(idOrder ?: "").child("latTujuan").setValue(latTujuan)
        ref.child(idOrder ?: "").child("lonTujuan").setValue(lonTujuan)
        ref.child(idOrder ?: "").child("lokasiTujuan").setValue(tujuan.text.toString())

        ref.child(idOrder ?: "").child("status").setValue(1)
        ref.child(idOrder ?: "").child("namaCustomer").setValue(Prefs.getString(Constant.NAMA_CUSTOMER, ""))
        ref.child(idOrder ?: "").child("nomorTelepon").setValue(Prefs.getString(Constant.TELEPON, ""))
        ref.child(idOrder ?: "").child("token").setValue(Prefs.getString(Constant.TOKEN, ""))
        ref.child(idOrder ?: "").child("uidCustomer")
            .setValue(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))

        val notif = NotificationMessage()
        val base = FirebaseMessagingMessage()
        val item = ItemChat()
        item.isMe = "false"
        item.message = "Penawaranmu ada yang ambil"
        item.timeStamp = HourToMillis.millis().toString()

        base.data = item
        notif.token = regid
        notif.message = base

        NetworkModule.getServiceFcm()
            .actionSendMessage(notif)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe()

        val i = Intent(this@MiBikeActivity, WaitingActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.putExtra("key", idOrder.toString())
        i.putExtra("from", Constant.TB_BIKE)
        startActivity(i)
    }

    private fun insertFirebase() {
        val myref = Config.databaseInstance(Constant.TB_BIKE)
        val key = myref.push().key

        val booking = CarBikeBooking()
        val time = Calendar.getInstance()
        val idOrder = time.timeInMillis

        booking.namaCustomer = Prefs.getString(Constant.NAMA_CUSTOMER, "")
        booking.token = Prefs.getString(Constant.TOKEN, FirebaseInstanceId.getInstance().token)
        booking.nomorTelepon = Prefs.getString(Constant.TELEPON, "")
        booking.foto = Prefs.getString(Constant.FOTO, "")

        booking.latAwal = latAwal
        booking.lonAwal = lonAwal
        booking.latTujuan = latTujuan
        booking.lonTujuan = lonTujuan
        booking.jarak = jarakTrip.text.toString()
        booking.tanggal = idOrder
        booking.idOrder = idOrder.toString()
        booking.harga = harga
        booking.status = 5
        booking.type = 2
        booking.lokasiAwal = asal.text.toString()
        booking.lokasiTujuan = tujuan.text.toString()
        booking.driver = ""
        booking.uidCustomer = Config.authInstanceCurrentUser()

        myref.child(idOrder.toString()).setValue(booking)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val notificationBooking = NotificationBooking()
                    val firebaseBooking = FirebaseBooking()

                    firebaseBooking.title = "Orderan MiBike"
                    firebaseBooking.deskripsi = asal.text.toString() + " - " + tujuan.text.toString()
                    firebaseBooking.book = booking
                    firebaseBooking.type = 2

                    notificationBooking.token = "/topics/mibike"
                    notificationBooking.booking = firebaseBooking

                    NetworkModule.getServiceFcm()
                        .actionSendBook(notificationBooking)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe()


//                    startActivity<WaitingActivity>("key" to key, "from" to Constant.TB_BIKE_ORDER)
                    val i = Intent(this@MiBikeActivity, WaitingActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("key", idOrder.toString())
                    i.putExtra("from", Constant.TB_BIKE)
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
                        BitmapDescriptorFactory.fromResource(R.drawable.pos_2)
                    )
                    showMarker(
                        latAwal,
                        lonAwal,
                        place.name.toString(),
                        BitmapDescriptorFactory.fromResource(R.drawable.pos_1)
                    )
                    showBound()
                    route()
                }

                val name = place.name ?: place.address
                asal.text = name
            } else if (requestCode == 2) {
                if (asal.text.length > 0) {
                    mMap?.clear()

                    showMarker(
                        latAwal,
                        lonAwal,
                        place.name.toString(),
                        BitmapDescriptorFactory.fromResource(R.drawable.pos_1)
                    )
                }

                latTujuan = place.latLng.latitude
                lonTujuan = place.latLng.longitude

                val name = place.name ?: place.address
                tujuan.text = name

                showMarker(
                    latTujuan,
                    lonTujuan,
                    place.name.toString(),
                    BitmapDescriptorFactory.fromResource(R.drawable.pos_2)
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

        val hargaAwal = tarif?.toInt()?.let { valueBulat.times(it) }
        harga = hargaAwal?.toInt()
        val resultHarga = ChangeFormat.toRupiahFormat2("$hargaAwal")
        if (valueBulat < Constant.JARAK_MAKSIMAL) {
            jarakTrip.text = text
            hargaTrip.text = "$resultHarga"

            if (hargaTrip.text.isNotEmpty()) {
                booking.background = resources.getDrawable(R.drawable.btn_background)
                booking.isEnabled = true
            }
        } else {
            alert {
                message = "Jaraknya Kejauhan Kak, Maaf"
                okButton {
                    jarakTrip.text = ""
                    hargaTrip.text = ""
                    booking.background = resources.getDrawable(R.drawable.btn_background_dis)
                    booking.isEnabled = false
                }
            }.show()
        }
    }

    private fun showBound() {
        if (latAwal != 0.0 && lonAwal != 0.0 && latTujuan != 0.0 && lonTujuan != 0.0)
            bottom.visibility = View.VISIBLE
        mMap?.setPadding(200, 0, 200, 0)
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

            showMarker(latAwal, lonAwal, "My Location", BitmapDescriptorFactory.fromResource(R.drawable.pos_1))

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

        return location.get(0).getAddressLine(0)
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
    }
}
