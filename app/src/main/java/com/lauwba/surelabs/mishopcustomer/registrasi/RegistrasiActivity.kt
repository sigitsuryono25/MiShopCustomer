package com.lauwba.surelabs.mishopcustomer.registrasi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lauwba.surelabs.mishopcustomer.MainActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*


class RegistrasiActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        FirebaseApp.initializeApp(applicationContext)

        mAuth = FirebaseAuth.getInstance()

        daftar.onClick {
            if (ktp.text.isEmpty()) {
                ktp.error = "Nomor KTP Tidak Boleh Kosong"
            } else if (nama.text.isEmpty()) {
                nama.error = "Nama Tidak Boleh Kosong"
            } else if (email.text.isEmpty()) {
                email.error = "Email Tidak Boleh Kosong"
            } else if (nomorTelepon.text.isEmpty()) {
                nomorTelepon.error = "Telepon Tidak Boleh Kosong"
            } else if (password.text.isEmpty()) {
                password.error = "Password Tidak Boleh Kosong"
            } else if (alamat.text.isEmpty()) {
                alamat.error = "Alamat Tidak Boleh Kosong"
            } else {
                alert {
                    message = "Dengan ini data yang saya masukkan adalah data yang sebenarnya. "
                    okButton {
                        mAuth?.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                            ?.addOnCompleteListener(this@RegistrasiActivity) {
                                if (it.isSuccessful) {
                                    insertIntoServer()
                                } else {
                                    toast("Terjadi Kesalahan")
                                }
                            }
                    }
                }.show()
            }
        }


    }

    private fun insertIntoServer() {
        val myref = Constant.database.getReference(Constant.TB_CUSTOMER)
        val key = myref.push().key
        val time = Calendar.getInstance()


        val customer = Customer()

        customer.key = key
        customer.email = email.text.toString()
        customer.noKTP = ktp.text.toString()
        customer.alamat = alamat.text.toString()
        customer.nama = nama.text.toString()
        customer.telepon = nomorTelepon.text.toString()
        customer.token = ""
        customer.terdaftar = time.timeInMillis
        customer.uid = Config.authInstanceCurrentUser()

        myref.child(key ?: "").setValue(customer)

        toast("Pendaftaran Berhasil")
        startActivity(intentFor<MainActivity>().newTask().clearTop())

    }
}
