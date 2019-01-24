package com.lauwba.surelabs.mishopcustomer

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lauwba.surelabs.mishopcustomer.config.Config
import com.project.lauwba.androidjsondemo.libs.RequestHandler
import org.json.JSONObject

class DetailRegistrasi : AppCompatActivity(), View.OnClickListener {


    lateinit var back: FloatingActionButton
    lateinit var finished: FloatingActionButton
    lateinit var alertDialog: AlertDialog.Builder
    lateinit var args: HashMap<String, String>
    lateinit var pd: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_registrasi)

        back = findViewById(R.id.back)
        finished = findViewById(R.id.finish)

        args = HashMap()

        finished.setOnClickListener(this)
    }

    inner class regProses() : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pd = ProgressDialog.show(this@DetailRegistrasi, "", "Mengirimkan Data...", false, false);
        }

        override fun doInBackground(vararg params: String?): String {
            var r = RequestHandler()
            args.put(Config.NAMA, "SIGIT SURYONO")
            args.put(Config.NAMA, "+6285201461240")
            args.put(Config.NAMA, "sigit2502")
            args.put(Config.NAMA, "sigitharsy25@gmail.com")
            args.put(Config.NAMA, "1")
            args.put(Config.NAMA, "PRAMBANAN")

            return r.sendPostRequest(Config.REG_URL, args)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!result.isNullOrEmpty()) {
                try {
                    var status: String = ""
                    var j = JSONObject(result)
                    var a = j.getJSONArray(Config.RES);
                    for (i in 0..a.length()) {
                        var c = a.getJSONObject(i)
                        status = c.getString("status")
                    }

                    if (status.equals("0")) {

                    } else {

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.finish -> {
                alertDialog = AlertDialog.Builder(this@DetailRegistrasi)
                alertDialog.setMessage("Apakah Data Sudah Benar ?")
                alertDialog.setPositiveButton("Ya") { dialogInterface: DialogInterface, i: Int ->
                    regProses().execute()
                }
                alertDialog.setNegativeButton("Tidak") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }

                alertDialog.create().show()
            }

            R.id.back -> {

            }
        }
    }
}
