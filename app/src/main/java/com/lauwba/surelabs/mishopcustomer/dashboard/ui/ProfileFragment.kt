package com.lauwba.surelabs.mishopcustomer.dashboard.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MainActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.profile.ProfileEditActivity
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_profile_fragment.*
import kotlinx.android.synthetic.main.loading.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity

class ProfileFragment : Fragment() {

    private var profileData: Customer? = null
    private var pd: ProgressDialog? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_profile_fragment, container, false)
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
        logout.onClick {
            Prefs.clear()
            Constant.mAuth.signOut()
            activity?.finish()
            startActivity(intentFor<MainActivity>().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        edit.onClick {
            startActivity<ProfileEditActivity>("data" to profileData)
        }
    }


    private fun setprofilData(profileData: Customer?) {
        name.text = profileData?.nama
        alamat.text = profileData?.alamat
        nomorTelepon.text = profileData?.telepon
        email.text = profileData?.email
        loading.visibility = View.GONE
        activity?.let {
            Glide.with(it)
                .load(profileData?.fotoCustomer)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(fotouser)
        }
        pd?.dismiss()
        content.visibility = View.VISIBLE
    }
}
