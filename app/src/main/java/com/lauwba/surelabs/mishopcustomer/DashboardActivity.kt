package com.lauwba.surelabs.mishopcustomer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {
    val gambar = arrayOf(
        R.drawable.fruit_crush_teaser,
        R.drawable.racing_monster_trucks_teaser,
        R.drawable.running_jack_teaser,
        R.drawable.smarty_bubbles
    )
    val url = arrayOf(
        "http://play.famobi.com/fruita-crush",
        "http://play.famobi.com/racing-monster-trucks",
        "http://play.famobi.com/running-jack",
        "http://play.famobi.com/smarty-bubbles"
    )

    var title = arrayOf(
        "Fruita Crush",
        "Racing Monster Trucks",
        "Running Jack",
        "Smartly Bubbles"
    )

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: GameAdapter
    lateinit var lm: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.listGame)
        recyclerView.setHasFixedSize(true)
//        lm = LinearLayoutManager(this@DashboardActivity)
        recyclerView.layoutManager = lm

        adapter = GameAdapter(title, gambar, url, this@DashboardActivity)
        recyclerView.adapter = adapter
    }
}
