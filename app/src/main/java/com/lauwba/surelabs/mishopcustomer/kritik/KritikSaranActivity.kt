@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.kritik

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.kritik.model.KritikModel
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_kritik_saran.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick

class KritikSaranActivity : AppCompatActivity() {

    private var pd: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kritik_saran)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleToolbar.text = "Kritik dan Saran"
        initView()
        loadDataCustomer()
    }

    private fun initView() {
        send.onClick {
            if (nama.text.isNullOrEmpty()) {
                nama.error = "Nama Wajib Diisi"
            } else if (isi.text.isNullOrEmpty()) {
                isi.error = "Komentar Wajib Diisi"
            } else {
                insertKritik()
            }
        }
    }

    private fun insertKritik() {
        pd = ProgressDialog.show(this, "", "Mengirimkan Kritik/Saran", false, false)
        val ref = Constant.database.getReference(Constant.TB_KRITIK)
        val model = KritikModel()
        val idkomentar = HourToMillis.millis()
        model.email_customer = Prefs.getString(Constant.EMAIL, "")
        model.poston = idkomentar
        model.komentar = isi.text.toString()
        ref.child(idkomentar.toString()).setValue(model)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    pd?.dismiss()
                    alert {
                        message = "Kritik/Saran berhasil Dikirimkan"
                        okButton {
                            finish()
                        }
                    }.show()
                }
            }

    }

    private fun loadDataCustomer() {
        pd = ProgressDialog.show(this, "", "Memuat Halaman...", false, false)
        val ref = Constant.database.getReference(Constant.TB_CUSTOMER)
        ref.orderByChild("email").equalTo(Prefs.getString(Constant.EMAIL, ""))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    pd?.dismiss()
                    if (p0.hasChildren()) {
                        for (issue in p0.children) {
                            val data = issue.getValue(Customer::class.java)
                            nama.setText(data?.nama.toString())
                        }
                    }
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
