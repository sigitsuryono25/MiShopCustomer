package com.lauwba.surelabs.mishopcustomer.myShop.ui.myshopdashoard


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
import com.lauwba.surelabs.mishopcustomer.myShop.adapter.MyShopTimelineAdapter
import com.lauwba.surelabs.mishopcustomer.myShop.model.MyShopModel
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.fragment_browse_my_shop.*
import kotlinx.android.synthetic.main.my_shop_activity.*

class BrowseMyShop : Fragment() {

    private var pd: ProgressDialog? = null
    private var mList: MutableList<MyShopModel>? = null
    private var mListMitra: MutableList<Customer>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_my_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mList = mutableListOf()
        mListMitra = mutableListOf()
        activity?.posting?.visibility = View.GONE
        getData()
    }

    private fun getData() {
        pd = ProgressDialog.show(activity, "", "Memuat halaman...", false, true)
        val ref = Constant.database.reference
        ref.child(Constant.TB_MYSHOP).limitToLast(25).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                pd?.dismiss()
                for (issues in p0.children) {
                    val data = issues.getValue(MyShopModel::class.java)
                    data?.let { mList?.add(it) }
                    val uid = data?.uid
                    Log.d("UID", uid)
                    val refs = Constant.database.getReference(Constant.TB_CUSTOMER)
                    refs.orderByChild("uid").equalTo(uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                for (issue in p0.children) {
                                    val mitraData = issue.getValue(Customer::class.java)
                                    mitraData?.let { mListMitra?.add(it) }
                                    setItemToAdapter(mList, mListMitra)
                                }
                            }
                        })
                }
            }
        })
    }

    private fun setItemToAdapter(
        mList: MutableList<MyShopModel>?,
        mitraData: MutableList<Customer>?
    ) {

        mList?.sortByDescending {
            it.tanggalPost
        }
        val adapter = MyShopTimelineAdapter(mList, mitraData)
        try {
            myShopRC.layoutManager = LinearLayoutManager(activity)
            myShopRC.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
