package com.example.olimpo_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.databinding.ActivityLoginBinding

class login_activity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textCreateNewAccount.setOnClickListener{
            startActivity(Intent(this, cadastro_activity::class.java))
            finish()
        }
    }
}