package com.example.olimpo_app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.databinding.ActivitySolicitacaoBinding

class SolicitacaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySolicitacaoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolicitacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}