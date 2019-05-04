package com.lauwba.surelabs.mishopcustomer.dashboard.ui

import android.app.ProgressDialog
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
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.new_activity_profile_fragment.*

class ProfileFragment : Fragment() {

    private var profileData: Customer? = null
    private var pd: ProgressDialog? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.new_activity_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pd = ProgressDialog.show(activity, "", "Memuat data profile...")
        val ref =Constant.database.getReference(Constant.TB_CUSTOMER)
        ref.orderByChild("uid").equalTo(Config.authInstanceCurrentUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.i("onCancelled", p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        for (data in p0.children) {
                            profileData = data.getValue(Customer::class.java)
                            setprofilData(profileData)
                        }
                    } else {

                    }
                }

            })

        initView()
    }


    private fun initView() {
//        logout.onClick {
//            Prefs.clear()
//            Constant.mAuth.signOut()
//            activity?.finish()
//            startActivity(intentFor<MainActivity>().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//        }
//        edit.onClick {
//            startActivity<ProfileEditActivity>("data" to profileData)
//        }
    }


    private fun setprofilData(profileData: Customer?) {
        name.setText(profileData?.nama)
        alamat.setText(profileData?.alamat)
        nomorTelepon.setText(profileData?.telepon)
//        email.text = profileData?.email
//        loading.visibility = View.GONE
//        activity?.let {
//            Glide.with(it)
//                .load(profileData?.fotoCustomer)
//                .apply(RequestOptions().centerCrop().circleCrop())
//                .into(fotouser)
//        }
        pd?.dismiss()
//        content.visibility = View.VISIBLE
    }
}
