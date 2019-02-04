package com.lauwba.surelabs.mishopcustomer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lauwba.surelabs.mishopcustomer.adapter.RssFeedModel
import com.synnapps.carouselview.CarouselView
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

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

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: GameAdapter
    lateinit var lm: RecyclerView.LayoutManager
    lateinit var news: CarouselView
    lateinit var c2c: CarouselView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.listGame)
        recyclerView.setHasFixedSize(true)
        lm = LinearLayoutManager(activity)
        recyclerView.layoutManager = lm

        adapter = GameAdapter(title, gambar, url, this!!.activity!!)
        recyclerView.adapter = adapter

        news = view.findViewById(R.id.newsView)
        c2c = view.findViewById(R.id.c2cView)




        c2c.setImageListener { position, imageView ->

            Glide.with(this!!.activity!!)
                .load(sampleImages[position])
                .into(imageView)
        }
        news.setImageListener { position, imageView ->

            Glide.with(this!!.activity!!)
                .load(sampleImages[position])
                .into(imageView)
        }

        news.pageCount = sampleImages.size
        c2c.pageCount = sampleImages.size
    }

    fun parseFeed(inputStream: InputStream): MutableList<RssFeedModel> {
        var items: MutableList<RssFeedModel> = mutableListOf()
        var isItem: Boolean = false
        var title: String = ""
        var link: String = ""
        var date: String = ""
        var desc: String = ""
        var src: String = ""
        try {
            var xmlPullParser = Xml.newPullParser()
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(inputStream, null)
            xmlPullParser.nextTag()
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                var eventType: Int = xmlPullParser.eventType
                var name: String = xmlPullParser.name

                if (name.equals(null, true)) {
                    continue
                }

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equals("item", true)) {
                        isItem = false
                    }
                    continue
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equals("item", true)) {
                        isItem = true
                        continue
                    }
                }

                var result = ""
                if (xmlPullParser.next() === XmlPullParser.TEXT) {
                    result = xmlPullParser.text
                    xmlPullParser.nextTag()
                }

                if (name.equals("item", true)) {
                    title = result
                } else if (name.equals("link", true)) {
                    link = result
                } else if (name.equals("enclosure", true)) {
                    date = result
                } else if (name.equals("description", true)) {
                    desc = result
                    try {
                        var document = Jsoup.parse(desc)
                        src = document.select("img").first().attr("src")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (title != null && link != null && date != null && desc != null) {
                    if (isItem) {
                        var item = RssFeedModel(title, link, date, src)
                        items.add(item)
                    } else {

                    }
                }
            }
        } catch (e: XmlPullParserException) {

        } catch (i: IOException) {

        } finally {
            inputStream.close()
        }
        return items
    }
}
