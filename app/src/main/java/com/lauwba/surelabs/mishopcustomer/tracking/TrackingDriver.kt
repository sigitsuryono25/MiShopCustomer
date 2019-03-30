package com.lauwba.surelabs.mishopcustomer.tracking

import android.content.Intent
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
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiCarActivity
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
    private var itemMitra: ItemMitra? = null
    private var from: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_driver)


        book = intent.getSerializableExtra("booking") as CarBikeBooking
        from = intent.getStringExtra("from")



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

        message.onClick {
            if (itemMitra == null)
            else
                startActivity<ChatActivity>("token" to itemMitra)
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
                                break
                            }
                        }
                    }
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
                    showData(itemMitra, BitmapDescriptorFactory.fromResource(R.drawable.driver_icon4))
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

        Glide.with(this)
            .load(item?.foto)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(fotoMitra)

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
