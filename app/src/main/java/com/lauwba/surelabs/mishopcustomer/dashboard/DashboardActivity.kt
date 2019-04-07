package com.lauwba.surelabs.mishopcustomer.dashboard

import android.Manifest
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking.CarBikeBooking
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.*
import com.lauwba.surelabs.mishopcustomer.kritik.KritikSaranActivity
import com.lauwba.surelabs.mishopcustomer.services.MyLocationService
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.tentang.TentangActivity
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast

class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

        switchOn.visibility = View.VISIBLE
        switchOnCheck()
        setBadges()
        checkPermissions()
        checkTransaksiMiCar()

        if (!Prefs.getBoolean(Constant.SERVICE, false)) {
            switchOn.isChecked = false
            toast("Layanan Belum Diaktifkan")
        } else {
            switchOn.isChecked = true
            Prefs.putBoolean(Constant.SERVICE, true)
        }


        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.container,
                            HomeFragment()
                        ).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.orderan -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.container,
                            ProsesFragmentNew()
                        ).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.inbox -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.container,
                            InboxFragment()
                        ).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.notifikasi -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, NewNotificationFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.akun -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, ProfileFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
        try {
            val notif = intent.getIntExtra("notif", 0)
            if (notif == 1024) {
                navigation.selectedItemId = R.id.notifikasi
                supportFragmentManager.beginTransaction().replace(
                    R.id.container,
                    NewNotificationFragment()
                ).commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.container,
                        HomeFragment()
                    ).commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        startService<MyLocationService>()
                    }

                    // check for permanent denial of any permission
                    if (report?.isAnyPermissionPermanentlyDenied == true) {
                        // show alert dialog navigating to Settings
                        Config.showSettingsDialog(this@DashboardActivity)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).withErrorListener { toast("Error occurred") }.onSameThread().check()
    }

    private fun setBadges() {
        val bottomNavigationMenuView = navigation.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(2)
        val itemView = v as BottomNavigationItemView

        val badge = LayoutInflater.from(this)
            .inflate(R.layout.badges, itemView, true)
        val count = badge.findViewById<TextView>(R.id.notifications)
    }

    fun checkTransaksiMiCar() {
        val ref = Constant.database.getReference(Constant.TB_CAR)
        ref.orderByChild("uidCustomer").equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        for (issues in p0.children) {
                            val data = issues.getValue(CarBikeBooking::class.java)
                            if (data?.rating.isNullOrEmpty()) {
                                val uidMitra = data?.driver
                                getMitraItem(uidMitra, data?.idOrder, Constant.TB_CAR)
                            }
                        }
                    } else {
                        checkTransaksiMiBike()
                    }
                }
            })
    }

    fun checkTransaksiMiBike() {
        val ref = Constant.database.getReference(Constant.TB_BIKE)
        ref.orderByChild("uidCustomer").equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        for (issues in p0.children) {
                            val data = issues.getValue(CarBikeBooking::class.java)
                            if (data?.rating.isNullOrEmpty()) {
                                val uidMitra = data?.driver
                                getMitraItem(uidMitra, data?.idOrder, Constant.TB_CAR)
                            }
                        }
                    } else {
                        checkTransaksiMiExpress()
                    }
                }
            })
    }

    fun checkTransaksiMiExpress() {
        val ref = Constant.database.getReference(Constant.TB_EXPRESS)
        ref.orderByChild("uidCustomer").equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        for (issues in p0.children) {
                            val data = issues.getValue(CarBikeBooking::class.java)
                            if (data?.rating.isNullOrEmpty()) {
                                val uidMitra = data?.driver
                                getMitraItem(uidMitra, data?.idOrder, Constant.TB_CAR)
                            }
                        }
                    } else {
                        checkTransakskMiShop()
                    }
                }
            })
    }

    fun checkTransakskMiShop() {

    }

    fun getMitraItem(uidMitra: String?, idOrder: String?, table: String) {
        var itemMitra: ItemMitra?
        val ref = Constant.database.getReference(Constant.TB_MITRA)
        ref.orderByChild("uidCustomer").equalTo(uidMitra)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        itemMitra = issue.getValue(ItemMitra::class.java)
                        checkRatingTransaksi(itemMitra, idOrder, table)
                    }
                }
            })

    }

    fun checkRatingTransaksi(
        item: ItemMitra?,
        idOrder: String?,
        table: String
    ) {
        val l = LayoutInflater.from(this@DashboardActivity)
        val v = l.inflate(R.layout.layout_rating_mitra, null)
        val ad = AlertDialog.Builder(this@DashboardActivity)
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
        rating.idOrder = idOrder
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

    private fun switchOnCheck() {
        switchOn.onCheckedChange { buttonView, isChecked ->
            if (isChecked) {
                try {
                    FirebaseMessaging.getInstance().subscribeToTopic("mishopcustomer")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("MISHOP", "successful subcsribe")
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    FirebaseMessaging.getInstance().subscribeToTopic("miservicecustomer")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("MISERVICE", "successful subcsribe")
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    FirebaseMessaging.getInstance().subscribeToTopic("micarcustomer")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("MICAR", "successful subcsribe")
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    FirebaseMessaging.getInstance().subscribeToTopic("mibikecustomer")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("MIEXPRESS", "successful subcsribe")
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
//                    sharedPreferences = getPreferences(Context.MODE_PRIVATE)
//                    sharedPreferences?.edit()?.putBoolean(Constant.SERVICE, true)?.apply()
                    Prefs.putBoolean(Constant.SERVICE, true)
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, HomeFragment())
//                        .commit()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("mishopcustomer")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MISHOP", "successful unsubscribeFromTopic")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                FirebaseMessaging.getInstance().unsubscribeFromTopic("miservicecustomer")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MISERVICE", "successful unsubscribeFromTopic")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                FirebaseMessaging.getInstance().unsubscribeFromTopic("micarcustomer")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MICAR", "successful unsubscribeFromTopic")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                FirebaseMessaging.getInstance().unsubscribeFromTopic("mibikecustomer")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MIBIKE", "successful unsubscribeFromTopic")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                Prefs.putBoolean(Constant.SERVICE, false)
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.container, HomeFragment())
//                    .commit()

            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.tentang -> {
                startActivity<TentangActivity>()
                return true
            }
            R.id.kritik -> {
                startActivity<KritikSaranActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
