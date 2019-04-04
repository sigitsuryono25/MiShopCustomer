package com.lauwba.surelabs.mishopcustomer.tentang

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.lauwba.surelabs.mishopcustomer.R
import kotlinx.android.synthetic.main.toolbar.*

class TentangActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
