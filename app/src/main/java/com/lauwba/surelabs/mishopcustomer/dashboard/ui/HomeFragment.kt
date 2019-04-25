package com.lauwba.surelabs.mishopcustomer.dashboard.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiBikeActivity
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiCarActivity
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiXpressActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.Constant.TB_CUSTOMER
import com.lauwba.surelabs.mishopcustomer.dashboard.adapter.GameAdapter
import com.lauwba.surelabs.mishopcustomer.dashboard.adapter.RssFeedAdapter
import com.lauwba.surelabs.mishopcustomer.dashboard.model.GameModel
import com.lauwba.surelabs.mishopcustomer.dashboard.model.RssFeedModel
import com.lauwba.surelabs.mishopcustomer.myShop.MyShopActivity
import com.lauwba.surelabs.mishopcustomer.myShop.detail.DetailMyShopActivity
import com.lauwba.surelabs.mishopcustomer.myShop.model.MyShopModel
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.lauwba.surelabs.mishopcustomer.service.ServiceActivity
import com.lauwba.surelabs.mishopcustomer.shop.ShopActivity
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_home_fragment.*
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.net.URL


class HomeFragment : Fragment(), YouTubeThumbnailView.OnInitializedListener {
    private var youTubeThumbnailLoader: YouTubeThumbnailLoader? = null
    private var thumbnailView: YouTubeThumbnailView? = null
    private val gambar = arrayOf(
        R.drawable._1000_blocks_teaser,
        R.drawable.drag_racing_club_teaser,
        R.drawable.duo_cards_teaser,
        R.drawable.fruit_crush_teaser,
        R.drawable.jewelish_teaser,
        R.drawable.knife_rain_teaser,
        R.drawable.onet_connect_classic_teaser,
        R.drawable.racing_monster_trucks_teaser,
        R.drawable.running_jack_teaser,
        R.drawable.smarty_bubbles
    )
    private val url = arrayOf(
        "https://play.famobi.com/1000-blocks",
        "https://play.famobi.com/drag-racing-club",
        "https://play.famobi.com/duo-cards",
        "https://play.famobi.com/fruita-crush",
        "https://play.famobi.com/jewelish",
        "https://play.famobi.com/knife-rain",
        "https://play.famobi.com/onet-connect-classic",
        "https://play.famobi.com/racing-monster-trucks",
        "https://play.famobi.com/running-jack",
        "https://play.famobi.com/smarty-bubbles"
    )

    private var title = arrayOf(
        "1000 Blocks",
        "Drag Racing Club",
        "Duo Cards",
        "Fruita Crush",
        "Jewelish",
        "Knife Rain",
        "Onet Connect Classic",
        "Racing Monster Trucks",
        "Running Jack",
        "Smartly Bubbles"
    )


//    private var sampleImages = arrayOf(
//        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113239-2-trans-studio-mini-maguwo-001-tantri-setyorini.jpg",
//        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113239-1-the-world-landmarks-merapi-park-001-tantri-setyorini.jpg",
//        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113239-3-pantai-nglambor-001-tantri-setyorini.jpg",
//        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113240-4-jogja-bay-waterpark-001-tantri-setyorini.jpg",
//        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113240-5-kebun-teh-nglinggo-001-tantri-setyorini.jpg"
//    )

    private var image: MutableList<String>? = null
    private var titleNews: MutableList<String>? = null
    private var linkNews: MutableList<String>? = null
    private val xml = "http://rss.detik.com/index.php/inet"
    private var adapter: GameAdapter? = null
    private var rssList: ArrayList<RssFeedModel>? = null
    private var gameList: MutableList<GameModel>? = null
    private var rssAdapter: RssFeedAdapter? = null
    private var imageC2c: MutableList<String>? = null
    private var idC2c: MutableList<String>? = null
    private var layanan: Boolean? = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image = mutableListOf()
        titleNews = mutableListOf()
        linkNews = mutableListOf()
        gameList = mutableListOf()
        imageC2c = mutableListOf()
        idC2c = mutableListOf()
        initOnlick()
    }

    private fun checkLayanan(): Int {
        var status = 0
        val ref = Constant.database.getReference(TB_CUSTOMER)
        ref.orderByChild("email").equalTo(Prefs.getString(Constant.EMAIL, ""))
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issues in p0.children) {
                            val data = issues.getValue(Customer::class.java)
                            status = data?.statusAktif ?: 0
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        return status
    }

    private fun setBackgroundTintMode() {
        toast("Aktifkan layanan terlebih dahulu")
    }

    private fun initOnlick() {
        miShop.onClick {
            if (checkLayanan() != 2)
                activity?.startActivity<ShopActivity>()
            else
                alert {
                    message = "Sedang ada Orderan Aktif. Silahkan Menunggu hingga Selesai"
                    okButton {

                    }
                }.show()
        }

        miService.onClick {
            if (checkLayanan() != 2)
                activity?.startActivity<ServiceActivity>()
            else
                alert {
                    message = "Sedang ada Orderan Aktif. Silahkan Menunggu hingga Selesai"
                    okButton {

                    }
                }.show()
        }

        miCar.onClick {
            if (checkLayanan() != 2)
                activity?.startActivity<MiCarActivity>()
            else
                alert {
                    message = "Sedang ada Orderan Aktif. Silahkan Menunggu hingga Selesai"
                    okButton {

                    }
                }.show()
        }

        miBike.onClick {
            if (checkLayanan() != 2)
                activity?.startActivity<MiBikeActivity>()
            else
                alert {
                    message = "Sedang ada Orderan Aktif. Silahkan Menunggu hingga Selesai"
                    okButton {

                    }
                }.show()
        }

        miXpress.onClick {
            if (checkLayanan() != 2)
                activity?.startActivity<MiXpressActivity>()
            else
                alert {
                    message = "Sedang ada Orderan Aktif. Silahkan Menunggu hingga Selesai"
                    okButton {

                    }
                }.show()
        }

        reload.onClick {
            initNews()
        }

        myShop.onClick {
            startActivity<MyShopActivity>()
        }

        try {
            initNews()
            initGames()
            initC2C()
            initYoutube()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initYoutube() {
        preview.initialize(getString(R.string.google_api_key), this)
    }

    private fun initC2C() {
        val ref = Constant.database.getReference(Constant.TB_MYSHOP)
        ref.orderByChild("uid").equalTo(Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid))
            .limitToFirst(5)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        if (p0.hasChildren()) {
                            imageC2c?.removeAll(imageC2c!!)
                            idC2c?.removeAll(idC2c!!)
                            for (issue in p0.children) {
                                val data = issue.getValue(MyShopModel::class.java)
                                data?.image?.let { imageC2c?.add(it) }
                                data?.idMyShop?.let { idC2c?.add(it) }
                                setToCarousel(imageC2c, idC2c)
                            }
                        } else {
                            noData.visibility = View.VISIBLE
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun setToCarousel(imageC2c: MutableList<String>?, idC2c: MutableList<String>?) {
        try {
            c2cView.setImageListener { position, imageView ->

                activity?.let {
                    Glide.with(it)
                        .load(imageC2c?.get(position))
                        .into(imageView)
                }
            }
            c2cView.pageCount = imageC2c?.size ?: 0

            c2cView.setImageClickListener {
                //                toast(idC2c?.get(it).toString())
                startActivity<DetailMyShopActivity>("idMyShop" to idC2c?.get(it))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun initGames() {

//        val ref = Constant.database.reference
//        ref.child("games").addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                for (issue in p0.children) {
//                    val data = issue.getValue(GameModel::class.java)
//                    data?.let { gameList?.add(it) }
//                }
//            }
//        })
//
//        listGame.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        adapter = gameList?.let { activity?.let { it1 -> GameAdapter(it, it1) } }
//        listGame.adapter = adapter

        for (i in 0 until gambar.size) {
            val item = GameModel(url[i], gambar[i], title[i])
            gameList?.add(item)
        }
        listGame.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = activity?.let { GameAdapter(gameList!!, it) }
        listGame.adapter = adapter
    }

    private fun initNews() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val url = URL(xml)
        val inputStream = url.openConnection().getInputStream()
        rssList = parseFeed(inputStream)
        if (rssList?.size ?: 0 > 0) {
            loading.visibility = View.GONE
            rssAdapter = activity?.let {
                RssFeedAdapter(
                    rssList!!,
                    it
                )
            }
            rcNews.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            rcNews.adapter = rssAdapter
        }

    }

    private fun parseFeed(inputStream: InputStream): ArrayList<RssFeedModel> {
        var title: String? = null
        var link: String? = null
        var date: String? = null
        var desc: String?
        var src: String? = null
        var isItem = false
        val items = ArrayList<RssFeedModel>()

        try {
            val xmlPullParser = Xml.newPullParser()
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(inputStream, null)

            xmlPullParser.nextTag()
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                val eventType = xmlPullParser.eventType

                val name = xmlPullParser.name ?: continue

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equals("item", ignoreCase = true)) {
                        isItem = false
                    }
                    continue
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equals("item", ignoreCase = true)) {
                        isItem = true
                        continue
                    }
                }

//                Log.d("MainActivity", "Parsing name ==> $name")
                var result = ""
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.text
                    xmlPullParser.nextTag()
                }

                //                        mFeedTitle = title
//                        mFeedLink = link
//                        mFeedDescription = date
                when {
                    name.equals("title", ignoreCase = true) -> title = result
                    name.equals("link", ignoreCase = true) -> link = result
                    name.equals("enclosure", ignoreCase = true) -> date = result
                    name.equals("description", ignoreCase = true) -> {
                        desc = result
                        try {
                            val document = Jsoup.parse(desc)
                            src = document.select("img").first().attr("src")
//                            Log.d("link", link)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                if (title != null && link != null && date != null) {
                    if (isItem) {
                        val item =
                            RssFeedModel(title, link, src ?: "")
                        items.add(item)
                    } else {
//                        mFeedTitle = title
//                        mFeedLink = link
//                        mFeedDescription = date
                    }

                    title = null
                    link = null
                    date = null
                    isItem = false
                }
            }
            return items
        } finally {
            inputStream.close()
        }
    }

    override fun onInitializationSuccess(p0: YouTubeThumbnailView?, p1: YouTubeThumbnailLoader?) {
        youTubeThumbnailLoader = p1
        p1?.setOnThumbnailLoadedListener(object : YouTubeThumbnailLoader.OnThumbnailLoadedListener {
            override fun onThumbnailLoaded(p0: YouTubeThumbnailView?, p1: String?) {
                Log.d("YOUTUBE", "THUMBNAIL LOADED")
                try {
                    loadingVideo.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onThumbnailError(p0: YouTubeThumbnailView?, p1: YouTubeThumbnailLoader.ErrorReason?) {
                Log.d("YOUTUBE", "THUMBNAIL ERROR ${p1.toString()}")
                try {
                    loadingVideo.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })

        youTubeThumbnailLoader?.setVideo(Constant.PLAYER)
        p0?.setOnClickListener {
            val url = "https://www.youtube.com/watch?v=${Constant.PLAYER}"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }

    override fun onInitializationFailure(p0: YouTubeThumbnailView?, p1: YouTubeInitializationResult?) {
        toast("onInitializationFailure ${p1.toString()}")

    }
}
