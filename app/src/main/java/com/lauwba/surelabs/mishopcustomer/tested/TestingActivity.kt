package com.lauwba.surelabs.mishopcustomer.tested

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.libs.RequestHandler
import kotlinx.android.synthetic.main.activity_testing.*
import org.json.JSONObject

class TestingActivity : AppCompatActivity() {

    val req = RequestHandler()
    val provinsi = mutableListOf<String>()
    val kabupaten = mutableListOf<String>()
    private var pd: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        GetProvinsi().execute()
        initView()
    }

    private fun initView() {
        provinsi.add("--Silahkan Pilih--")
        province.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!province.selectedItem.toString().equals("--Silahkan Pilih--", true)) {
                    getidProv(province.selectedItem.toString())
                }
            }

        }
    }

    inner class GetKab : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            return req.sendGetRequest(Constant.URL_KAB + params[0])
        }

        override fun onPreExecute() {
            super.onPreExecute()
            pd = ProgressDialog.show(this@TestingActivity, "Tunggu", "Sedang Memanggil Daftar Kabupaten", false, true)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                pd?.dismiss()
                kabupaten.removeAll(kabupaten)
                val j = JSONObject(result)
                val ja = j.getJSONArray("data")
                for (i in 0 until ja.length()) {
                    val obj = ja.getJSONObject(i)
                    val idKab = obj.getString("id")
                    val name = obj.getString("name")
                    val show = "$idKab-$name"
                    kabupaten.add(show)
                }

                val adapter = ArrayAdapter(this@TestingActivity, android.R.layout.simple_dropdown_item_1line, kabupaten)
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                kab.adapter = adapter
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    inner class GetProvinsi : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            return req.sendGetRequest(Constant.URL_PROVINSI)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            pd = ProgressDialog.show(this@TestingActivity, "Tunggu", "Sedang Memanggil Daftar Provinsi", false, true)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                pd?.dismiss()
                val j = JSONObject(result)
                val ja = j.getJSONArray("data")
                provinsi.removeAll(provinsi)
                for (i in 0 until ja.length()) {
                    val obj = ja.getJSONObject(i)
                    val idProv = obj.getString("id")
                    val name = obj.getString("name")
                    val show = "$idProv-$name"
                    provinsi.add(show)
                }

                val adapter = ArrayAdapter(this@TestingActivity, android.R.layout.simple_dropdown_item_1line, provinsi)
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                province.adapter = adapter
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getidProv(text: String?) {
        val init = text?.split("-")
        val id = init?.get(0)
        GetKab().execute(id)
    }
}
