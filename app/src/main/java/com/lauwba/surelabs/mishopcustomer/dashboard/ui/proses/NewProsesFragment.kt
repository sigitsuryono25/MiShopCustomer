@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.dashboard.ui.proses

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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
        var i: Int? = null
        fun newInstance(k: String?, from: Int?): Fragment {
            kind = k
            i = from
            Log.d("Kind", kind)
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
        if (i == 0) {
            getData()
        } else if (i == 1 || i == 2 || i == 3) {
            getDataCarBikeExpress()
        } else if (i == 4) {
            getDataService()
        }
    }

    private fun getDataService() {
        pd = ProgressDialog.show(activity, "", "Memuat Jadwal", false, true)
        try {
            val shop = kind?.let { Constant.database.getReference(it) }
            shop?.orderByChild("uidCustomer")
                ?.equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            pd?.dismiss()
                            list?.removeAll(list!!)
                            if (p0.hasChildren()) {
                                for (issue in p0.children) {
                                    val data = issue.getValue(ProsesModel::class.java)
                                    if (data?.status != 4) {
                                        data?.let { list?.add(it) }
                                        setToAdapter(list)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getData() {
        pd = ProgressDialog.show(activity, "", "Memuat history", false, true)
        try {
            val shop = kind?.let { Constant.database.getReference(it) }
            shop?.orderByChild("uidCustomer")
                ?.equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            pd?.dismiss()
                            list?.removeAll(list!!)
                            if (p0.hasChildren()) {
                                for (issue in p0.children) {
                                    val data = issue.getValue(ProsesModel::class.java)
                                    if (data?.status_order_shop != 4) {
                                        data?.let { list?.add(it) }
                                        setToAdapter(list)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDataCarBikeExpress() {
        pd = ProgressDialog.show(activity, "", "Memuat history", false, true)
        try {
            val shop = kind?.let { Constant.database.getReference(it) }
            shop?.orderByChild("uidCustomer")
                ?.equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            list?.removeAll(list!!)
                            pd?.dismiss()
                            if (p0.hasChildren()) {
                                for (issue in p0.children) {
                                    val data = issue.getValue(ProsesModel::class.java)
                                    if (data?.status != 0 && data?.status != 3 && data?.status != 4) {
                                        data?.let { list?.add(it) }
                                        setToAdapter(list)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setToAdapter(list: MutableList<ProsesModel>?) {
        try {
            val adapter = list?.let { i?.let { it1 -> ProsesAdapter(it, it1) } }
            val lm = LinearLayoutManager(activity)
            lm.reverseLayout = true
            lm.stackFromEnd = true
            listProses.layoutManager = lm
            listProses.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
