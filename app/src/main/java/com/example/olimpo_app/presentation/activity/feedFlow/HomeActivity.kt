package com.example.olimpo_app.presentation.activity.feedFlow

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.olimpo_app.R
import com.example.olimpo_app.databinding.ActivityHomeBinding
import com.example.olimpo_app.presentation.activity.accessFlow.MainActivity
import com.example.olimpo_app.presentation.fragment.feedFlow.CreatePublicationFragment
import com.example.olimpo_app.presentation.fragment.feedFlow.FeedFragment
import com.example.olimpo_app.presentation.fragment.messageFlow.ListCommunitiesChatsFragment
import com.example.olimpo_app.presentation.fragment.negotiationFlow.ShopFragment
import com.example.olimpo_app.presentation.fragment.accessFlow.UserProfileFragment
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(applicationContext)
        setContentView(binding.root)

        loadCommunityDetails()

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<FeedFragment>(R.id.fragment)
        }

        binding.btHome.setOnClickListener {
            binding.btHome.setImageResource(R.drawable.home_icon)
            binding.btChat.setImageResource(R.drawable.comments)
            binding.btAddPublication.setImageResource(R.drawable.plus_square)
            binding.btShop.setImageResource(R.drawable.shop_icon)
            binding.btProfile.setImageResource(R.drawable.profile)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<FeedFragment>(R.id.fragment)
            }
        }
        binding.btChat.setOnClickListener {
            binding.btHome.setImageResource(R.drawable.home_icon_)
            binding.btChat.setImageResource(R.drawable.comments_)
            binding.btAddPublication.setImageResource(R.drawable.plus_square)
            binding.btShop.setImageResource(R.drawable.shop_icon)
            binding.btProfile.setImageResource(R.drawable.profile)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<ListCommunitiesChatsFragment>(R.id.fragment)
            }
        }
        binding.btAddPublication.setOnClickListener {
            binding.btHome.setImageResource(R.drawable.home_icon_)
            binding.btChat.setImageResource(R.drawable.comments)
            binding.btAddPublication.setImageResource(R.drawable.plus_square_)
            binding.btShop.setImageResource(R.drawable.shop_icon)
            binding.btProfile.setImageResource(R.drawable.profile)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<CreatePublicationFragment>(R.id.fragment)
            }
        }
        binding.btShop.setOnClickListener {
            binding.btHome.setImageResource(R.drawable.home_icon_)
            binding.btChat.setImageResource(R.drawable.comments)
            binding.btAddPublication.setImageResource(R.drawable.plus_square)
            binding.btShop.setImageResource(R.drawable.shop_icon_)
            binding.btProfile.setImageResource(R.drawable.profile)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<ShopFragment>(R.id.fragment)
            }
        }
        binding.btProfile.setOnClickListener {
            binding.btHome.setImageResource(R.drawable.home_icon_)
            binding.btChat.setImageResource(R.drawable.comments)
            binding.btAddPublication.setImageResource(R.drawable.plus_square)
            binding.btShop.setImageResource(R.drawable.shop_icon)
            binding.btProfile.setImageResource(R.drawable.profile_)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<UserProfileFragment>(R.id.fragment)
            }
        }

        binding.btnVoltar.setOnClickListener {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
        }
    }
    private fun loadCommunityDetails(){
        binding.communityName.text = preferenceManager.getString(Constants.KEY_COMMUNITY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_COMMUNITY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.perfilImage.setImageBitmap(bitmap)
    }
}