package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.R
import com.example.olimpo_app.databinding.ActivityDashBoardBinding

class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonArrow.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
        val webView = findViewById<WebView>(R.id.webview)
        val load = findViewById<ProgressBar>(R.id.progressBar2)
        webView.loadUrl("https://app.powerbi.com/view?r=eyJrIjoiZDU2ZDY2YzYtMjg5MS00YTQxLWJkNGEtZDJmNzJkNDBhMTE2IiwidCI6ImIxNDhmMTRjLTIzOTctNDAyYy1hYjZhLTFiNDcxMTE3N2FjMCJ9")
        webView.settings.setJavaScriptEnabled(true)

        webView.webViewClient =object: WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                load.visibility = View.VISIBLE
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                load.visibility = View.INVISIBLE
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && binding.webview.canGoBack()){
            binding.webview.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}