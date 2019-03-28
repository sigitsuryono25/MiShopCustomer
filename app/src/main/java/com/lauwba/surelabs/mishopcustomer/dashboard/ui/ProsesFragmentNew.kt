package com.lauwba.surelabs.mishopcustomer.dashboard.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.proses.NewProsesFragment
import kotlinx.android.synthetic.main.fragment_proses.*

class ProsesFragmentNew : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_proses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeFragment(NewProsesFragment.newInstance(Constant.TB_SHOP_ORDER))

        tabs.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miShop -> {
                    changeFragment(NewProsesFragment.newInstance(Constant.TB_SHOP_ORDER))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miService -> {
                    changeFragment(NewProsesFragment.newInstance(Constant.TB_SERVICE_ORDER))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miBike -> {
                    changeFragment(NewProsesFragment.newInstance(Constant.TB_BIKE))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miCar -> {
                    changeFragment(NewProsesFragment.newInstance(Constant.TB_CAR))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miXpress -> {
                    changeFragment(NewProsesFragment.newInstance(Constant.TB_EXPRESS))
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    private fun changeFragment(f: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.container, f)
            .commit()
    }
}
