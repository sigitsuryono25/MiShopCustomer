package com.lauwba.surelabs.mishopcustomer.config

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object Config {

    val HOST = "http://192.168.100.6/Mi-ShopWeb/"
    val REG_URL = HOST + "registrasi-customer"


    //param intent
    val URL = "url"

    //Fieldset
    val NO_TELP = "notelp"
    val PASS = "pass"
    val NAMA = "nama"
    val EMAIL = "email"
    val GENDER = "gender"
    val ALAMAT = "alamat"

    //array result
    val RES = "result"

    val REGISTRATION_COMPLETE = "registrasi complete"
//    val database = FirebaseDatabase.getInstance()

    fun databaseInstance(table: String): DatabaseReference {
        return table.let { FirebaseDatabase.getInstance().getReference(it) }
    }

    fun authInstanceCurrentUser(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun showSettingsDialog(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            openSettings(activity)
        })
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()

    }

    private fun openSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}