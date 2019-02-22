package com.lauwba.surelabs.mishopcustomer.dashboard.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.activity_profile_fragment.*
import kotlinx.android.synthetic.main.loading.*

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var ref = Config.databaseInstance(Config.tb_customer)
        ref.orderByChild("uid").equalTo(Config.authInstanceCurrentUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.i("onCancelled", p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        for (data in p0.children) {
                            var profileData = data.getValue(Customer::class.java)
                            setprofilData(profileData)
                        }
                    } else {

                    }
                }

            })
    }

    private fun setprofilData(profileData: Customer?) {
        name.text = profileData?.nama
        alamat.text = profileData?.alamat
        nomorTelepon.text = profileData?.telepon
        email.text = profileData?.email
        loading.visibility = View.GONE
    }
}
