//package com.lauwba.surelabs.mishopcustomer.appintro.newregistrasi
//
//import android.os.Bundle
//import android.support.v4.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import com.lauwba.surelabs.mishopcustomer.R
//import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
//import kotlinx.android.synthetic.main.fragment_addressing.*
//import kotlinx.android.synthetic.main.toolbar.*
//
//class AddressingFragment : Fragment() {
//
//    private var kab: MutableList<String>? = null
//    private var message: String? = null
//    private var oap: OnAddressingPass? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_addressing, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        activity?.titleToolbar?.text = "Pengaturan Alamat"
//        kab = mutableListOf()
//        setItemList(kab)
//    }
//
//    private fun setItemList(kab: MutableList<String>?) {
//        kab?.add("--Silahkan Pilih Satu--")
//        kab?.add("KABUPATEN JEMBRANA")
//        kab?.add("KABUPATEN TABANAN")
//        kab?.add("KABUPATEN BADUNG")
//        kab?.add("KABUPATEN GIANYAR")
//        kab?.add("KABUPATEN KLUNGKUNG")
//        kab?.add("KABUPATEN BANGLI")
//        kab?.add("KABUPATEN KARANG ASEM")
//        kab?.add("KABUPATEN BULELENG")
//        kab?.add("KOTA DENPASAR")
//
//        val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, kab)
//        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
//        kabupaten.adapter = adapter
//        adapter.notifyDataSetChanged()
//    }
//
//    interface OnAddressingPass {
//        fun onAddressingPass(c: Customer)
//    }
//
//
//}
