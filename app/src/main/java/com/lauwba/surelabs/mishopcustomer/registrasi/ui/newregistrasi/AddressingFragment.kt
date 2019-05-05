package com.lauwba.surelabs.mishopcustomer.registrasi.ui.newregistrasi

import agency.tango.materialintroscreen.SlideFragment
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.libs.RequestHandler
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.fragment_addressing.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONObject

class AddressingFragment : SlideFragment() {

    val req = RequestHandler()
    val provinsi = mutableListOf<String>()
    val kabupaten = mutableListOf<String>()
    private var pd: ProgressDialog? = null
    private var oap: OnAddressingPass? = null
    private var message: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addressing, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        oap = context as OnAddressingPass
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.text = "Pengaturan Alamat"
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

        provTitle.onClick {
            GetProvinsi().execute()
        }
    }

    override fun canMoveFurther(): Boolean {
        if (alamat.text.toString().isEmpty()) {
            message = "Alamat Harus Diisi"
            return false
        } else if (kab.selectedItem.toString().contains("--Silahkan Pilih Satu--", true)) {
            message = "Kabupaten Harus Dipilih"
            return false
        } else if (!agree.isChecked) {
            message = "Silahkan terima persyaratan"
            return false
        } else {
            val c = Customer()
            c.alamat = alamat.text.toString()
            c.statusAktif = 0
            c.masaSuspend = 0
            c.terdaftar = HourToMillis.millis()
            c.kabupaten = getTextSpinner(kab.selectedItem.toString())
            c.provinsi = getTextSpinner(province.selectedItem.toString())

            oap?.onAddressingPass(c)
            return true
        }
    }

    override fun cantMoveFurtherErrorMessage(): String? {
        return message
    }

    override fun buttonsColor(): Int {
        return R.color.blue_gj
    }

    override fun backgroundColor(): Int {
        return android.R.color.white
    }

    interface OnAddressingPass {
        fun onAddressingPass(c: Customer)
    }

    inner class GetKab : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            return req.sendGetRequest(Constant.URL_KAB + params[0])
        }

        override fun onPreExecute() {
            super.onPreExecute()
            pd = ProgressDialog.show(activity, "Tunggu", "Sedang Memanggil Daftar Kabupaten", false, true)
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

                val adapter = ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, kabupaten)
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
            pd = ProgressDialog.show(activity, "Tunggu", "Sedang Memanggil Daftar Provinsi", false, true)
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

                val adapter = ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, provinsi)
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

    private fun getTextSpinner(text: String?): String? {
        val init = text?.split("-")
        val id = init?.get(1)
        return id
    }


}
