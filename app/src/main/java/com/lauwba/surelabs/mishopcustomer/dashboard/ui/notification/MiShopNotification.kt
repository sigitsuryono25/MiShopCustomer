package com.lauwba.surelabs.mishopcustomer.dashboard.ui.notification

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model.Distance
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.network.NetworkModule
import com.lauwba.surelabs.mishopcustomer.notification.NotificationHandler
import com.lauwba.surelabs.mishopcustomer.notification.model.NotifikasiAdapter
import com.lauwba.surelabs.mishopcustomer.notification.model.NotifikasiItem
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_content_notification.*

class MiShopNotification : Fragment() {

    private var list: MutableList<NotifikasiItem>? = null
    private var pd: ProgressDialog? = null
    private var dis: CompositeDisposable? = null

    companion object {
        var kind: String? = null
        var from: Int? = null
        fun newInstance(k: String?, i: Int?): Fragment {
            kind = k
            from = i
            return MiShopNotification()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = mutableListOf()
        if (from == 0) {
            getData()
        } else if (from == 4) {
            getDataService()
        } else if (from == 1 || from == 2 || from == 3) {
            getDataCarBikeExpress()
        }
    }

    private fun getDataService() {
        pd = ProgressDialog.show(activity, "", "Memuat notifikasi...")
        val shop = Constant.database.reference
        try {
            kind?.let {
                shop.child(it).limitToLast(10).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        pd?.dismiss()
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(NotifikasiItem::class.java)
//                                data?.let { list?.add(it) }
//                                setToAdapter(list)
                                checkDistance(
                                    Prefs.getDouble("lat", Constant.LAT_DEFAULT),
                                    Prefs.getDouble("lon", Constant.LON_DEFAULT),
                                    data?.lat,
                                    data?.lon,
                                    data,
                                    "Penawaran Mi-Service"
                                )
                            }
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getData() {
        pd = ProgressDialog.show(activity, "", "Memuat notifikasi...")
        val shop = Constant.database.reference
        try {
            kind?.let {
                shop.child(it).limitToLast(10).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        pd?.dismiss()
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(NotifikasiItem::class.java)
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
                                        data,
                                        "Penawaran Mi-Shop"
                                    )
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

    private fun checkDistance(
        latAwal: Double?,
        lonAwal: Double?,
        latTujuan: Double?,
        lonTujuan: Double?,
        post: NotifikasiItem?,
        title: String?
    ) {
        dis = CompositeDisposable()
        val origin = "$latAwal, $lonAwal"
        val destination = "$latTujuan, $lonTujuan"

        dis?.add(
            NetworkModule.getService().actionRoute(
                origin,
                destination,
                "AIzaSyDSMub1sBU2AnCOr8_MKJV2JC_c8I0UOsM"
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val route = it?.routes?.get(0)
                    val overview = route?.overviewPolyline
                    val point = overview?.points
                    val distance = route?.legs?.get(0)?.distance

                    hitungJarak(distance, post, title)
                }, {

                })
        )
    }

    private fun hitungJarak(
        distance: Distance?,
        post: NotifikasiItem?,
        title: String?
    ) {
        val jarak = distance?.value

        val valueBagi = jarak?.div(1000)
        val valueBulat = Math.ceil(valueBagi?.toDouble() ?: 0.0)
        Log.d("JARAK", valueBulat.toString())

        if (valueBulat <= 50.0) {
            post?.let { list?.add(it) }
            setToAdapter(list)
            val notif = NotificationHandler(activity)
            notif.sendNotification(title, post?.deskripsi)
        }

    }

    private fun getDataCarBikeExpress() {
        pd = ProgressDialog.show(activity, "", "Memuat notifikasi...")
        val shop = Constant.database.reference
        try {
            kind?.let {
                shop.child(it).limitToLast(10).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        pd?.dismiss()
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(NotifikasiItem::class.java)
                                if (data?.status == 0) {
//                                    data.let { list?.add(it) }
//                                    setToAdapter(list)
                                    checkDistance(
                                        Prefs.getDouble("lat", Constant.LAT_DEFAULT),
                                        Prefs.getDouble("lon", Constant.LON_DEFAULT),
                                        data.latAwal,
                                        data.lon,
                                        data,
                                        "Penawaran $kind"
                                    )
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

    private fun setToAdapter(list: MutableList<NotifikasiItem>?) {
        list?.sortByDescending {
            it.idOrder
        }
        try {
            val adapter = list?.let { from?.let { it1 -> NotifikasiAdapter(it, activity, it1) } }
            notifikasiList.layoutManager = LinearLayoutManager(activity)
            notifikasiList.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
