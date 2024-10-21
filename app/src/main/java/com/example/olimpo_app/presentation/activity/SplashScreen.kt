package com.example.olimpo_app.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.R
import com.example.olimpo_app.presentation.activity.accessFlow.LoginActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(kotlinx.coroutines.Runnable { abrirtela() }, 1000)
    }

    private fun abrirtela() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }
}