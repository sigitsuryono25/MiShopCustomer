package com.lauwba.surelabs.mishopcustomer.tracking

import android.content.ContentResolver
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
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
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.chat.ChatActivity
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.dashboard.DashboardActivity
import com.lauwba.surelabs.mishopcustomer.dashboard.Rating
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_tracking_driver.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class TrackingDriver : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var book: CarBikeBooking? = null
    private var phone: String? = null
    private var dari: String? = null
    private var itemMitra: ItemMitra? = null
    private var platData: Kendaraan? = null
    private var from: String? = null
    private var bm: BitmapDescriptor? = null
    //tatus: 0  posting, 1 diambil customer/mitra, 2 tiba, 3 sampai/selesai, 4  batal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_driver)
        book = intent.getSerializableExtra("booking") as CarBikeBooking
        from = intent.getStringExtra("from")
        when (from) {
            Constant.TB_CAR -> {
                bm = BitmapDescriptorFactory.fromResource(R.drawable.car)
                dari = Constant.TB_CAR
            }
            Constant.TB_BIKE -> {
                bm = BitmapDescriptorFactory.fromResource(R.drawable.cycle)
                dari = Constant.TB_BIKE
            }
            Constant.TB_EXPRESS -> {
                bm = BitmapDescriptorFactory.fromResource(R.drawable.cycle)
                dari = Constant.TB_EXPRESS
            }
        }
        homeprice.text = "Rp. " + ChangeFormat.toRupiahFormat2(book?.harga.toString())
        homeAwal.text = book?.lokasiAwal
        homeTujuan.text = book?.lokasiTujuan
        homeWaktudistance.text = book?.jarak


        val fragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        fragment.getMapAsync(this)

        homebuttonnext.onClick {
            finish()
        }

        call.onClick {
            phone?.let { it1 -> makeCall(it1) }
        }

        sms.onClick {
            phone?.let { it1 -> sendSMS(it1) }
        }

        message.onClick {
            if (itemMitra == null)
            else
                startActivity<ChatActivity>("token" to itemMitra)
        }

    }

    private fun orderListener(from: String, idOrder: String?) {
        val ref = Constant.database.getReference(from)
        ref.orderByChild("idOrder").equalTo(idOrder)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issues in p0.children) {
                            val data = issues.getValue(CarBikeBooking::class.java)
                            val status = data?.status
                            showAlert(status)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun showAlert(status: Int?) {
        val message: String
        val ab = AlertDialog.Builder(this@TrackingDriver)
        val v = LayoutInflater.from(this@TrackingDriver).inflate(R.layout.layout_dialog_order, null)
        ab.setView(v)
        val messageTv = v.findViewById<TextView>(R.id.message)
        val driverTV = v.findViewById<TextView>(R.id.namaDriver)
        val platTv = v.findViewById<TextView>(R.id.plat)
        val fotoMitras = v.findViewById<ImageView>(R.id.fotoMitra)

        when (status) {
            1 -> {
                message = "Sedang Menjemput"
                statusAntar.text = message
            }
            2 -> {
                message = "Mitra Mishop Telah Tiba"
                statusAntar.text = message
                driverTV.text = itemMitra?.nama_mitra
                platTv.text = plat.text.toString()
                messageTv.text = message
                try {
                    Glide.with(this)
                        .load(itemMitra?.foto)
                        .apply(RequestOptions().centerCrop().circleCrop())
                        .into(fotoMitras)
                    val defaultSoundUri =
                        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.driver_found)
                    val r = RingtoneManager.getRingtone(this@TrackingDriver, defaultSoundUri)
                    r.play()

                    ab.setPositiveButton("Siap") { dialog, which ->
                        dialog.dismiss()
                    }

                    val ac = ab.create()
                    ac.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            3 -> {
                message = "Selesai Mengantarkan"
                statusAntar.text = message
                driverTV.text = driverName.text.toString()
                platTv.text = plat.text.toString()
                messageTv.text = message
                try {
                    Glide.with(this)
                        .load(itemMitra?.foto)
                        .apply(RequestOptions().centerCrop().circleCrop())
                        .into(fotoMitras)
                    val defaultSoundUri =
                        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.driver_found)
                    val r = RingtoneManager.getRingtone(this@TrackingDriver, defaultSoundUri)
                    r.play()

                    ab.setPositiveButton("Siap") { dialog, which ->
                        finish()
                    }

                    val ac = ab.create()
                    ac.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            4 -> {
                message = "Dibatalkan oleh mitra"
                statusAntar.text = message
                driverTV.text = driverName.text.toString()
                platTv.text = plat.text.toString()
                messageTv.text = message
                try {
                    Glide.with(this)
                        .load(itemMitra?.foto)
                        .apply(RequestOptions().centerCrop().circleCrop())
                        .into(fotoMitras)
                    val defaultSoundUri =
                        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.driver_found)
                    val r = RingtoneManager.getRingtone(this@TrackingDriver, defaultSoundUri)
                    r.play()
                    ab.setPositiveButton("Siap") { dialog, which ->
                        dialog.dismiss()
                    }

                    val ac = ab.create()
                    ac.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }

    private fun ratingListener(itemMitra: ItemMitra?) {
        val ref = from?.let { Constant.database.getReference(it) }
        ref?.orderByChild("idOrder")?.equalTo(book?.idOrder)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        for (issue in p0.children) {
                            val data = issue.getValue(CarBikeBooking::class.java)
                            if (data?.status == 3) {
                                from?.let { checkRatingTransaksi(itemMitra, book?.idOrder, it) }
                            }
                        }
                    }
                    return
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(intentFor<DashboardActivity>().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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
                    itemMitra = issue.getValue(ItemMitra::class.java)
                    phone = itemMitra?.no_tel
                    ratingListener(itemMitra)
                    showData(itemMitra)
                }
            }

        })
    }

    fun checkRatingTransaksi(
        item: ItemMitra?,
        idOrder: String?,
        table: String
    ) {
        val l = LayoutInflater.from(this@TrackingDriver)
        val v = l.inflate(R.layout.layout_rating_mitra, null)
        val ad = AlertDialog.Builder(this@TrackingDriver)
        ad.setView(v)
        val ratingbar = v.findViewById<RatingBar>(R.id.itemRating)
        val fotoMitra = v.findViewById<ImageView>(R.id.imageItem)
        val namaMitra = v.findViewById<TextView>(R.id.nameText)
        val kirimRating = v.findViewById<Button>(R.id.kirimRating)
        val ulasan = v.findViewById<TextInputEditText>(R.id.ulasan)

        try {
            Glide.with(this)
                .load(item?.foto)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(fotoMitra)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        namaMitra.text = item?.nama_mitra


        val ac = ad.create()
        ac.show()

        kirimRating.onClick {
            if (ratingbar.rating == 0f) {
                toast("Kasih Rating Dulu kak Buat Mitranya :)")
            } else {
                sendRating(item, ratingbar.rating, ulasan.text.toString(), ac, idOrder, table)
            }
        }
    }

    private fun sendRating(
        item: ItemMitra?,
        rate: Float,
        ulasan: String,
        ad: AlertDialog,
        idOrder: String?,
        table: String
    ) {
        val ref = Constant.database.getReference(Constant.RATING)
        val key = ref.push().key
        val rating = Rating()
        rating.rating = rate
        rating.comment = ulasan
        rating.key = key
        rating.uidMitra = item?.uid
        rating.user = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)

        ref.child(key ?: HourToMillis.millis().toString()).setValue(rating)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Constant.database.getReference(table).child(idOrder ?: "")
                        .child("rating").setValue("done")
                    toast("Terima kasih untuk rating dan ulasanya kak :)")
                } else
                    toast("Terjadi kesalahan")

                ad.dismiss()
            }
    }

    private fun showData(driver: ItemMitra?) {
        mMap?.clear()
        val coordinate = LatLng(driver?.lat ?: 0.0, driver?.lon ?: 0.0)
        val destinasi = LatLng(book?.latTujuan ?: 0.0, book?.lonTujuan ?: 0.0)
        mMap?.addMarker(
            MarkerOptions().position(destinasi).title(book?.lokasiTujuan).icon(
                BitmapDescriptorFactory.fromResource(
                    R.drawable.ic_pin1
                )
            )
        )?.showInfoWindow()
        mMap?.addMarker(
            MarkerOptions().position(coordinate).title(driver?.nama_mitra).icon(
                bm
            )
        )?.showInfoWindow()
//        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12f))

        mMap?.setOnInfoWindowClickListener {
            //            startActivity()
        }

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.40).toInt()

        val bound = LatLngBounds.builder()
        bound.include(destinasi)
        bound.include(coordinate)

        mMap?.setOnMapLoadedCallback {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), width, height, padding))
        }

        try {
            Glide.with(this@TrackingDriver)
                .load(driver?.foto)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(driverImage)

            driverName.text = driver?.nama_mitra
            val ref = Constant.database.getReference("kendaraan")
            ref.orderByChild("driver").equalTo(driver?.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            for (issue in p0.children) {
                                platData = issue.getValue(TrackingDriver.Kendaraan::class.java)
                                val platdata = platData?.plat
                                val jenis = platData?.jenis
                                plat.text = "$platdata\n$jenis"

                                dari?.let { orderListener(it, book?.idOrder) }
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

    class Kendaraan {
        var driver: String? = null
        var plat: String? = null
        var jenis: String? = null
    }

}
