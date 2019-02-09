package com.lauwba.surelabs.mishopcustomer.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Config
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.loading.*

class WebViewActivity : AppCompatActivity() {

    lateinit var wv: WebView
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web_view)

        wv = findViewById(R.id.webView)

        wv.settings.javaScriptEnabled = true
        wv.settings.javaScriptCanOpenWindowsAutomatically = true

        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

            }
        }

        wv.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loading.visibility = View.GONE
            }
        }

        try {
            var i: Intent = intent
            val gameOrNot = intent.getIntExtra("status", 0)
            if (gameOrNot == 1) {
                url.visibility = View.GONE
            }
            url.setText(intent.getStringExtra(Config.URL))
            wv.loadUrl(i.getStringExtra(Config.URL))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
