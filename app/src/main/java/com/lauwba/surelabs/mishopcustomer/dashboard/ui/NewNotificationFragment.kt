package com.lauwba.surelabs.mishopcustomer.dashboard.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.dashboard.ui.notification.MiShopNotification
import kotlinx.android.synthetic.main.fragment_new_notification.*

class NewNotificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeFragment(MiShopNotification.newInstance(Constant.TB_SHOP, 0))

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miShop -> {
                    changeFragment(MiShopNotification.newInstance(Constant.TB_SHOP, 0))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miService -> {
                    changeFragment(MiShopNotification.newInstance(Constant.TB_SERVICE, 4))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miBike -> {
                    changeFragment(MiShopNotification.newInstance(Constant.TB_BIKE, 2))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miCar -> {
                    changeFragment(MiShopNotification.newInstance(Constant.TB_CAR, 1))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miXpress -> {
                    changeFragment(MiShopNotification.newInstance(Constant.TB_EXPRESS, 3))
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    private fun changeFragment(f: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.containerNotif, f)
            .commit()
    }


}
