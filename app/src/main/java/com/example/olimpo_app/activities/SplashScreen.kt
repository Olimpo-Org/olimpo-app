package com.example.olimpo_app.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(kotlinx.coroutines.Runnable { abrirtela() }, 1000)
    }

    private fun abrirtela() {
        val intent = Intent(this, login_activity::class.java)
        startActivity(intent)

    }
}