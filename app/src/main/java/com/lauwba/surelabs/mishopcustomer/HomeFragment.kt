package com.lauwba.surelabs.mishopcustomer

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
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiBikeActivity
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiCarActivity
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiXpress
import com.lauwba.surelabs.mishopcustomer.shop.ShopActivity
import com.lauwba.surelabs.mishopcustomer.webview.adapter.GameAdapter
import com.lauwba.surelabs.mishopcustomer.webview.adapter.RssFeedAdapter
import com.lauwba.surelabs.mishopcustomer.webview.model.GameModel
import com.lauwba.surelabs.mishopcustomer.webview.model.RssFeedModel
import kotlinx.android.synthetic.main.activity_home_fragment.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.net.URL


class HomeFragment : Fragment() {

    val gambar = arrayOf(
        R.drawable.fruit_crush_teaser,
        R.drawable.racing_monster_trucks_teaser,
        R.drawable.running_jack_teaser,
        R.drawable.smarty_bubbles
    )
    val url = arrayOf(
        "https://play.famobi.com/fruita-crush",
        "https://play.famobi.com/racing-monster-trucks",
        "https://play.famobi.com/running-jack",
        "https://play.famobi.com/smarty-bubbles"
    )

    var sampleImages = arrayOf(
        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113239-2-trans-studio-mini-maguwo-001-tantri-setyorini.jpg",
        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113239-1-the-world-landmarks-merapi-park-001-tantri-setyorini.jpg",
        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113239-3-pantai-nglambor-001-tantri-setyorini.jpg",
        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113240-4-jogja-bay-waterpark-001-tantri-setyorini.jpg",
        "https://cdns.klimg.com/merdeka.com/i/w/news/2018/05/21/978617/content_images/670x335/20180521113240-5-kebun-teh-nglinggo-001-tantri-setyorini.jpg"
    )

    var title = arrayOf(
        "Fruita Crush",
        "Racing Monster Trucks",
        "Running Jack",
        "Smartly Bubbles"
    )
    var image: MutableList<String>? = null
    var titleNews: MutableList<String>? = null
    var linkNews: MutableList<String>? = null
    val xml = "http://rss.detik.com/index.php/inet"
    var adapter: GameAdapter? = null
    var rssList: ArrayList<RssFeedModel>? = null
    var gameList: MutableList<GameModel>? = null
    var rssAdapter: RssFeedAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image = mutableListOf()
        titleNews = mutableListOf()
        linkNews = mutableListOf()
        gameList = mutableListOf()

        c2cView.setImageListener { position, imageView ->

            activity?.let {
                Glide.with(it)
                    .load(sampleImages[position])
                    .into(imageView)
            }
        }

        c2cView.pageCount = sampleImages.size


        miShop.onClick {
            activity?.startActivity<ShopActivity>()
        }

        miCar.onClick {
            activity?.startActivity<MiCarActivity>()
        }

        miBike.onClick {
            activity?.startActivity<MiBikeActivity>()
        }

        miXpress.onClick {
            activity?.startActivity<MiXpress>()
        }

        reload.onClick {
            initNews()
        }

        try {
            initNews()
            initGames()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun initGames() {
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
            rssAdapter = activity?.let { RssFeedAdapter(rssList!!, it) }
            rcNews.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            rcNews.adapter = rssAdapter
        }

    }

    fun parseFeed(inputStream: InputStream): ArrayList<RssFeedModel> {
        var title: String? = null
        var link: String? = null
        var date: String? = null
        var desc: String? = null
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

                Log.d("MainActivity", "Parsing name ==> $name")
                var result = ""
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.text
                    xmlPullParser.nextTag()
                }

                if (name.equals("title", ignoreCase = true)) {
                    title = result
                } else if (name.equals("link", ignoreCase = true)) {
                    link = result
                } else if (name.equals("enclosure", ignoreCase = true)) {
                    date = result
                } else if (name.equals("description", ignoreCase = true)) {
                    desc = result
                    try {
                        val document = Jsoup.parse(desc)
                        src = document.select("img").first().attr("src")
                        Log.d("link", link)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                if (title != null && link != null && date != null) {
                    if (isItem) {
                        val item = RssFeedModel(title, link, src ?: "")
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
}
