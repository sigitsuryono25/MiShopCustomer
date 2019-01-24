package com.lauwba.surelabs.mishopcustomer

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lauwba.surelabs.mishopcustomer.config.Config

class WebViewActivity : AppCompatActivity() {

    lateinit var wv: WebView
    lateinit var pd: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        wv = findViewById(R.id.webView)

        wv.settings.javaScriptEnabled = true
        wv.settings.javaScriptCanOpenWindowsAutomatically = true

        pd = ProgressDialog.show(this, "", "", false, false)
        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

            }
        }

        wv.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                pd.dismiss()
            }
        }

        try {
            var i: Intent = intent
            wv.loadUrl(i.getStringExtra(Config.URL))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
