@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.login

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.dashboard.DashboardActivity
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var pd: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mAuth = FirebaseAuth.getInstance()
        email_sign_in_button.onClick {
            pd = ProgressDialog.show(this@LoginActivity, "", getString(R.string.loading), false, false)
            mAuth?.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        checkOnTableCustomer(email.text.toString())
                    } else {
                        toast("Terjadi Kesalahan")
                    }
                }
                ?.addOnFailureListener {
                    Log.i("OnFailure", it.message.toString())
                }
        }
    }

    private fun checkOnTableCustomer(email: String) {
        val ref = Config.databaseInstance(Constant.TB_CUSTOMER)
        ref.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    pd?.dismiss()
                    if (!p0.hasChildren()) {
                        alert(
                            "Email Tidak Ditemukan.\nPastikan anda Terdaftar sebagai customer sebelum melakukan login",
                            "Kesalahan"
                        ) {
                            okButton {
                            }
                        }.show()
                    } else {
                        for (issues in p0.children) {
                            val data = issues.getValue(Customer::class.java)
                            val token = FirebaseInstanceId.getInstance().token

                            ref.child(data?.key ?: "").child("token").setValue(token)
                            Prefs.putString(Constant.EMAIL, email)
                            Prefs.putString(Constant.UID, mAuth?.currentUser?.uid)
                            Prefs.putString(Constant.ALAMAT, data?.alamat)
                            Prefs.putString(Constant.NAMA_CUSTOMER, data?.nama)
                            Prefs.putString(Constant.TOKEN, token)
                            Prefs.putString(Constant.FOTO, data?.fotoCustomer)
                            Prefs.putString(Constant.TELEPON, data?.telepon)
                            toast(getString(R.string.welcome))
                            finish()
                            startActivity<DashboardActivity>()
                        }
                    }
                }
            })
    }


}
