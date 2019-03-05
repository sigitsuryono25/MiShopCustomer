package com.lauwba.surelabs.mishopcustomer.myShop.ui.myshopdashoard


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
import com.lauwba.surelabs.mishopcustomer.shop.adapter.TimeLineAdapter
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemMitra
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_browse_my_shop.*

class BrowseMyShop : Fragment() {

    private var pd: ProgressDialog? = null
    private var mList: MutableList<ItemPost>? = null
    private var mListMitra: MutableList<ItemMitra>? = null

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
                    val data = issues.getValue(ItemPost::class.java)
                    if (data?.uid?.equals(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)) == false) {
                        mList?.add(data)
                        val uid = data.uid
                        ref.child(Constant.TB_CUSTOMER).orderByChild("uid").equalTo(uid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val mitraData = issues.getValue(ItemMitra::class.java)
                                mitraData?.let { mListMitra?.add(it) }
                                setItemToAdapter(mList, mListMitra, 0)
                            }
                        })
                    }
                }
            }
        })
    }

    private fun setItemToAdapter(
        mList: MutableList<ItemPost>?,
        mitraData: MutableList<ItemMitra>?,
        tarif: Int?
    ) {

        mList?.sortByDescending {
            it.tanggalPost
        }
        val adapter = activity?.let { TimeLineAdapter(mList, it, mitraData, tarif) }
        try {
            myShopRC.layoutManager = LinearLayoutManager(activity)
            myShopRC.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
