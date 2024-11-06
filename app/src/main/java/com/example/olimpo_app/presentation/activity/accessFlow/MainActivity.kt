package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.repository.CommunityRepository
import com.example.olimpo_app.databinding.ActivityMainBinding
import com.example.olimpo_app.presentation.activity.BaseActivity
import com.example.olimpo_app.presentation.activity.feedFlow.HomeActivity
import com.example.olimpo_app.presentation.adapters.CommunityAdapter
import com.example.olimpo_app.presentation.listeners.CommunityClickListener
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import com.example.olimpo_app.utils.PreferenceManager
import kotlinx.coroutines.launch

class MainActivity : BaseActivity(), CommunityClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var communityAdapter: CommunityAdapter
    private var apiCommunityList: MutableList<CommunityAPI> = mutableListOf()
    private val communityRepository = CommunityRepository(AccessApiInstance.service)
    private val objectsLocalStorage = ObjectsLocalStorage()
    private var loggedUser: UserAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        loggedUser = objectsLocalStorage.getObjectFromLocalStorage(
            this,
            Constants.KEY_OBJ_USER,
            UserAPI::class.java
        )
        Log.d(
            "Caralho",
            "onCreate: ${loggedUser?.id} ${loggedUser?.name} ${loggedUser?.email} ${loggedUser?.profileImage}"
        )

        setListeners()
        getCommunityList()
        setAdapter()

        binding.encontrarComunidade.setOnClickListener {
            startActivity(Intent(applicationContext, FindCommunitiesActivity::class.java))
            finish()
        }
        binding.btnCreateCommunity.setOnClickListener {
            startActivity(Intent(applicationContext, CreateCommunityActivity::class.java))
            finish()
        }
        binding.imageSolicitation.setOnClickListener {
            startActivity(Intent(applicationContext, SolicitationActivity::class.java))
            finish()
        }
    }

    private fun getCommunityList() {
        loading(true)
        lifecycleScope.launch {
            try {
                loading(true)
                apiCommunityList = loggedUser?.id?.let {
                    communityRepository.getAllCommunitiesByUser(
                        it
                    ).body()?.toMutableList()
                } ?: mutableListOf()
                if (apiCommunityList.isNotEmpty()) {
                    setAdapter()
                    binding.conversationsRecyclerView.visibility = View.VISIBLE
                    binding.LinearLayout.visibility = View.GONE
                    loading(false)

                } else {
                    loading(false)
                    binding.conversationsRecyclerView.visibility = View.GONE
                    binding.LinearLayout.visibility = View.VISIBLE

                }
            } catch (e: Exception) {
                loading(false)
                showToast("Erro ao buscar comunidades")
                binding.conversationsRecyclerView.visibility = View.GONE
                binding.LinearLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun signOut() {
        showToast("Saindo...")
        preferenceManager.clear()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    override fun onCommunityClicked(communityAPI: CommunityAPI) {
        Log.d("MainActivity", "onCommunityClicked: $communityAPI")
        objectsLocalStorage.cleanObjectFromLocalStorage(this, Constants.KEY_OBJ_COMMUNITY)
        objectsLocalStorage.saveObjectInLocalStorage(this, Constants.KEY_OBJ_COMMUNITY, communityAPI)
        Log.d("MainActivity", "onCommunityClicked: ${objectsLocalStorage.getObjectFromLocalStorage(
            this,
            Constants.KEY_OBJ_COMMUNITY,
            CommunityAPI::class.java
        )}")
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setAdapter() {
        communityAdapter = CommunityAdapter(apiCommunityList,
            this
        )
        binding.conversationsRecyclerView.adapter = communityAdapter
    }

    private fun setListeners() {
        binding.imageSignOut.setOnClickListener { signOut() }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar?.visibility = View.VISIBLE
        } else {
            binding.progressBar?.visibility = View.INVISIBLE
        }
    }
}
