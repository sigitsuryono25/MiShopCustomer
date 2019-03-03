package com.lauwba.surelabs.mishopcustomer.myShop.ui.myshopdashoard


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lauwba.surelabs.mishopcustomer.R

class BrowseMyShop : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_my_shop, container, false)
    }


}
