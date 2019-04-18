package com.lauwba.surelabs.mishopcustomer.registrasi.ui.newregistrasi

import agency.tango.materialintroscreen.SlideFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.fragment_addressing.*
import kotlinx.android.synthetic.main.toolbar.*

class AddressingFragment : SlideFragment() {

    private var kab: MutableList<String>? = null
    private var message: String? = null
    private var oap: OnAddressingPass? = null

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
        activity?.titleToolbar?.text = "Pengaturan Alamat"
        kab = mutableListOf()
        setItemList(kab)
    }

    private fun setItemList(kab: MutableList<String>?) {
        kab?.add("--Silahkan Pilih Satu--")
        kab?.add("KABUPATEN JEMBRANA")
        kab?.add("KABUPATEN TABANAN")
        kab?.add("KABUPATEN BADUNG")
        kab?.add("KABUPATEN GIANYAR")
        kab?.add("KABUPATEN KLUNGKUNG")
        kab?.add("KABUPATEN BANGLI")
        kab?.add("KABUPATEN KARANG ASEM")
        kab?.add("KABUPATEN BULELENG")
        kab?.add("KOTA DENPASAR")

        val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, kab)
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        kabupaten.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun canMoveFurther(): Boolean {
        if (alamat.text.toString().isEmpty()) {
            message = "Alamat Harus Diisi"
            return false
        } else if (kabupaten.selectedItem.toString().contains("--Silahkan Pilih Satu--", true)) {
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
            c.kabupaten = kabupaten.selectedItem.toString()

            oap?.onAddressingPass(c)
            return true
        }
    }

    override fun cantMoveFurtherErrorMessage(): String? {
        return message
    }

    override fun buttonsColor(): Int {
        return R.color.micar
    }

    override fun backgroundColor(): Int {
        return R.color.mishop
    }

    interface OnAddressingPass {
        fun onAddressingPass(c: Customer)
    }


}
