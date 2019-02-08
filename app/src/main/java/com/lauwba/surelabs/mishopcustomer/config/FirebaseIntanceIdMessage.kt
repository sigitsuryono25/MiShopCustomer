package com.lauwba.surelabs.mi_shop.Service

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.lauwba.surelabs.mishopcustomer.config.Config


class FirebaseIntanceIdMessage : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        FirebaseApp.initializeApp(this);
        val refreshedToken = FirebaseInstanceId.getInstance().token

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        val registrationComplete = Intent(Config.REGISTRATION_COMPLETE)
        registrationComplete.putExtra("token", refreshedToken)
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
    }

    private fun sendRegistrationToServer(token: String?) {
        // sending gcm token to server
    }

    companion object {
        private val TAG = FirebaseIntanceIdMessage::class.java.simpleName
    }
}