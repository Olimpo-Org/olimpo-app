package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.Solicitation
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.repository.CommunityRepository
import com.example.olimpo_app.databinding.ActivityFindCommunitiesBinding
import com.example.olimpo_app.presentation.adapters.SolicitCommunityAdapter
import com.example.olimpo_app.presentation.listeners.SolicitListener
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import kotlinx.coroutines.launch
import java.util.UUID

class FindCommunitiesActivity : AppCompatActivity(), SolicitListener {

    private lateinit var binding: ActivityFindCommunitiesBinding
    private lateinit var communityAdapter: SolicitCommunityAdapter
    private val communityRepository = CommunityRepository(AccessApiInstance.service)
    private var communities: List<CommunityAPI> = listOf()
    private val objectsLocalStorage = ObjectsLocalStorage()
    private var loggedUserAPI: UserAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindCommunitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loggedUserAPI = objectsLocalStorage.getObjectFromLocalStorage(
            this,
            Constants.KEY_OBJ_USER,
            UserAPI::class.java
        )

        init()
        loadCommunities()
        setListeners()
    }

    private fun init() {
        communityAdapter = SolicitCommunityAdapter(communities, this)
        binding.conversationsRecyclerView.adapter = communityAdapter
    }

    private fun setListeners() {
        binding.buttonArrow.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
        binding.imageSolicitation.setOnClickListener {
            startActivity(Intent(applicationContext, SolicitationActivity::class.java))
            finish()
        }
        binding.buttonSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun loadCommunities() {
        lifecycleScope.launch {
            try {
                val response = loggedUserAPI?.id?.let {
                    communityRepository.getAllCommunitiesNotByUser(
                        it
                    )
                }
                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        communities = response.body()!!
                        communityAdapter.apply {
                            communityAdapter = SolicitCommunityAdapter(communities, this@FindCommunitiesActivity)
                            binding.conversationsRecyclerView.adapter = communityAdapter
                        }
                        binding.conversationsRecyclerView.visibility = if (communities.isNotEmpty()) View.VISIBLE else View.GONE
                    } else {
                        showToast("Failed to load communities")
                    }
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun signOut() {
        // Perform necessary actions for sign out
        showToast("Signing out...")
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    override fun onSolicitClicked(communityId: Int) {
        lifecycleScope.launch {
            val solicitation = loggedUserAPI?.id?.let {
                loggedUserAPI!!.name?.let { it1 ->
                    loggedUserAPI!!.profileImage?.let { it2 ->
                        Solicitation(
                            id = UUID.randomUUID(),
                            communityId = communityId,
                            userId = it,
                            userName = it1,
                            userUrlImage = it2

                        )
                    }
                }
            }
            try {
                val response = solicitation?.let { communityRepository.createSolicitation(it) }
                if (response != null) {
                    if (response.isSuccessful) {
                        showToast("Solicitation created successfully")
                    } else {
                        showToast("Failed to create solicitation")
                    }
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }
}
