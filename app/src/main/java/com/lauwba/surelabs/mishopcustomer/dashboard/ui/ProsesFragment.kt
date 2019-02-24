package com.lauwba.surelabs.mishopcustomer.dashboard.ui


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
import com.lauwba.surelabs.mishopcustomer.dashboard.adapter.ProsesRecyclerAdapter
import com.lauwba.surelabs.mishopcustomer.dashboard.model.ProsesModel
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_proses.*

class ProsesFragment : Fragment() {

    private var mList: MutableList<ProsesModel>? = null
    private var mListDeskripsi: MutableList<ItemPost>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mList = mutableListOf()
        mListDeskripsi = mutableListOf()

        getData()
    }

    private fun getData() {
        val ref = Constant.database.getReference(Constant.TB_SHOP_ORDER)
        val desk = Constant.database.getReference(Constant.TB_SHOP)
        ref.orderByChild("uidCustomer").equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        val data = issue.getValue(ProsesModel::class.java)
                        data?.let { mList?.add(it) }
                        val idOrder = data?.idOrder
                        desk.orderByChild("idOrder").equalTo(idOrder)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    for (i in p0.children) {
                                        val issues = i.getValue(ItemPost::class.java)
                                        issues?.let { mListDeskripsi?.add(it) }
                                        setToRc(mList, mListDeskripsi)
                                    }
                                }
                            })
                    }
                }

            })
    }

    private fun setToRc(mList: MutableList<ProsesModel>?, deskripsi: MutableList<ItemPost>?) {
        try {
            val adapter = mList?.let { activity?.let { it1 -> ProsesRecyclerAdapter(it, deskripsi, it1) } }
            rcProses.layoutManager = LinearLayoutManager(activity)
            rcProses.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
