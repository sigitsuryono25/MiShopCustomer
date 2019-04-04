package com.lauwba.surelabs.mishopcustomer.services

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.*
import com.pixplicity.easyprefs.library.Prefs

class MyLocationService : Service() {
    private var client: FusedLocationProviderClient? = null
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Location Service", "Service Started")
        updateLocation()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Location Service", "Service destroyed")
        if (client != null) {
            try {
                val voidTask = client?.removeLocationUpdates(MyLocationCallBack())
                if (voidTask?.isSuccessful == true) {
                    Log.d("Location Service", "StopLocation updates successful! ")
                } else {
                    Log.d("Location Service", "StopLocation updates unsuccessful! " + voidTask.toString())
                }
            } catch (exp: SecurityException) {
                Log.d("Location Service", " Security exception while removeLocationUpdates")
            }

        }
    }

    private fun updateLocation() {
        val req = LocationRequest()
        req.interval = 1000
        req.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        client = LocationServices.getFusedLocationProviderClient(this)
        val permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            client?.requestLocationUpdates(req, MyLocationCallBack(), null)
        }
    }


    private fun updateDatabase(lat: Double?, lon: Double?) {
        lat?.let { Prefs.putDouble("lat", it) }
        lon?.let { Prefs.putDouble("lon", it) }
//        val myRef = Constant.database.getReference(Constant.TB_MITRA)
//        val query = myRef.orderByChild("uidCustomer").equalTo(Constant.mAuth.currentUser?.uidCustomer)

//        var key = ""
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                for (issue in p0.children) {
//                    key = issue.key.toString()
//
//                    myRef.child(key).child("lat").setValue(lat)
//                    myRef.child(key).child("lon").setValue(lon)
//
//                }
//            }
//
//        })
    }

    private inner class MyLocationCallBack : LocationCallback() {

        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            super.onLocationResult(p0)

            val lat = p0?.lastLocation?.latitude
            val lon = p0?.lastLocation?.longitude
//                    toast("{$lat, $lon}")
            Log.d("LOCATION SERVICES", "{$lat, $lon}")

            updateDatabase(lat, lon)
        }

    }
}