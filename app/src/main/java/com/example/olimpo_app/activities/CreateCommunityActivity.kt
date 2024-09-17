package com.example.olimpo_app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.databinding.ActivityFindCommunitiesBinding

class CreateCommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindCommunitiesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindCommunitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}