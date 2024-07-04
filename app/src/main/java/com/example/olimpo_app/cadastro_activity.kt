package com.example.olimpo_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.databinding.ActivityCadastroBinding

class cadastro_activity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textLogin.setOnClickListener{
            startActivity(Intent(this, login_activity::class.java))
            finish()
        }
    }
}