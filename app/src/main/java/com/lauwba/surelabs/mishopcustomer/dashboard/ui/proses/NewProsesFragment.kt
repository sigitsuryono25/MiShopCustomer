@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.dashboard.ui.proses

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.dashboard.model.ProsesModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.new_proses_fragment.*

class NewProsesFragment : Fragment() {

    private var list: MutableList<ProsesModel>? = null
    private var pd: ProgressDialog? = null

    companion object {
        var kind: String? = null
        fun newInstance(k: String?): Fragment {
            kind = k
            return NewProsesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_proses_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = mutableListOf()
        getData()
    }

    private fun getData() {
        pd = ProgressDialog.show(activity, "", "Memuat history", false, true)
        try {
            val shop = kind?.let { Constant.database.getReferenceFromUrl(it) }
            shop?.orderByChild("uidCustomer")
                ?.equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
                ?.limitToLast(10)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        pd?.dismiss()
                        if (p0.hasChildren()) {
                            for (issue in p0.children) {
                                val data = issue.getValue(ProsesModel::class.java)
                                data?.let { list?.add(it) }
                                setToAdapter(list)
                            }
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setToAdapter(list: MutableList<ProsesModel>?) {
        list?.sortByDescending {
            it.idOrder
        }
        try {
            val adapter = list?.let { ProsesAdapter(it) }
            listProses.layoutManager = LinearLayoutManager(activity)
            listProses.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
