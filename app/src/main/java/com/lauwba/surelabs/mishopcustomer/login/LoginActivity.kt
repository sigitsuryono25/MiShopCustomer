package com.lauwba.surelabs.mishopcustomer.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.lauwba.surelabs.mishopcustomer.DashboardActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.loading.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mAuth = FirebaseAuth.getInstance()
        email_sign_in_button.onClick {
            mAuth?.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Prefs.putString(Config.EMAIL, email.text.toString())
                        toast("Selamat datang kembali")
                        loading.visibility = View.GONE
                        startActivity(intentFor<DashboardActivity>().clearTop().newTask())
                    } else {
                        toast("Terjadi Kesalahan")
                    }
                }
                ?.addOnFailureListener {
                    Log.i("OnFailure", it.message.toString())
                }
        }
    }


}
