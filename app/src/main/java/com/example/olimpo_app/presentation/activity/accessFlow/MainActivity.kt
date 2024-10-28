package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide.init
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.data.model.accessFlow.Community
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.repository.CommunityRepository
import com.example.olimpo_app.databinding.ActivityMainBinding
import com.example.olimpo_app.presentation.activity.BaseActivity
import com.example.olimpo_app.presentation.activity.feedFlow.HomeActivity
import com.example.olimpo_app.presentation.activity.messageFlow.ChatActivity
import com.example.olimpo_app.presentation.adapters.CommunityAdapter
import com.example.olimpo_app.presentation.listeners.ConversionListener
import com.example.olimpo_app.presentation.listeners.CommunityClickListener
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.JsonConverter
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : BaseActivity(), ConversionListener, CommunityClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var communityAdapter: CommunityAdapter
    private lateinit var communities: MutableList<Community>
    private lateinit var apiCommunityList : MutableList<CommunityAPI>
    private lateinit var database: FirebaseFirestore
    private val communityRepository = CommunityRepository(AccessApiInstance.service)
    private val jsonConverter = JsonConverter()
    private val userId by lazy { preferenceManager.getString(Constants.KEY_API_USER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)

        init()
        getToken()
        setListeners()
        getCommunityList()

        binding.encontrarComunidade.setOnClickListener {
            startActivity(Intent(applicationContext, FindCommunitiesActivity::class.java))
            finish()
        }
        binding.imageSolicitation.setOnClickListener {
            startActivity(Intent(applicationContext, SolicitacaoActivity::class.java))
            finish()
        }
        binding.btnCreateCommunity.setOnClickListener {
            startActivity(Intent(applicationContext, CreateCommunityActivity::class.java))
            finish()
        }
    }

    private fun getCommunityList() {
        lifecycleScope.launch {
            apiCommunityList = communityRepository.getAllCommunitiesByUser(
                userId = userId!!.toLong()
            ).body()?.toMutableList() ?: mutableListOf()

            if (apiCommunityList.isNotEmpty()) {
                apiCommunityList.forEach { apiCommunity ->
                    val community = Community(
                        name = apiCommunity.name,
                        image = apiCommunity.imageUrl,
                        communityApiId = apiCommunity.id.toString()
                    )
                    communities.add(community)
                }
                communityAdapter.notifyDataSetChanged()
                binding.conversationsRecyclerView.visibility = View.VISIBLE
                binding.LinearLayout.visibility = View.GONE
            } else {
                showToast("Erro ao buscar comunidades")
                binding.conversationsRecyclerView.visibility = View.GONE
                binding.LinearLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { updateToken(it) }
    }

    private fun updateToken(token: String) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token)
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID)!!)
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener { showToast("Indisponível para atualizar o token") }
    }

    private fun signOut() {
        showToast("Saindo...")
        val documentReference = preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID)?.let {
            database.collection(Constants.KEY_COLLECTION_USERS).document(it)
        }
        val updates = hashMapOf<String, Any>(Constants.KEY_FCM_TOKEN to FieldValue.delete())
        documentReference?.update(updates)
            ?.addOnSuccessListener {
                preferenceManager.clear()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
            ?.addOnFailureListener { showToast("Não foi possível sair :/") }
    }

    override fun onConversionClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }

    override fun onCommunityClicked(community: Community, communityAPI: CommunityAPI) {
        jsonConverter.saveObjectToJson(this, Constants.KEY_OBJ_COMMUNITY, community)
        database.collection(Constants.KEY_COLLECTION_COMMUNITY)
            .whereEqualTo(Constants.KEY_COMMUNITY_API_ID, community.communityApiId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val firebaseCommunity = documents.first()
                    val communityData = Community(
                        name = firebaseCommunity.getString(Constants.KEY_COMMUNITY_NAME),
                        image = firebaseCommunity.getString(Constants.KEY_COMMUNITY_IMAGE),
                        token = firebaseCommunity.getString(Constants.KEY_COMMUNITY_TOKEN),
                        id = firebaseCommunity.getString(Constants.KEY_COMMUNITY_ID),
                        communityApiId = community.communityApiId
                    )
                    communityData.name?.let {
                        preferenceManager.putString(Constants.KEY_COMMUNITY_NAME,
                            it
                        )
                    }
                    communityData.image?.let {
                        preferenceManager.putString(Constants.KEY_COMMUNITY_IMAGE,
                            it
                        )
                    }
                    communityData.id?.let {
                        preferenceManager.putString(Constants.KEY_COMMUNITY_ID,
                            it
                        )
                    }
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    showToast("Comunidade não encontrada no Firebase")
                }
            }
            .addOnFailureListener {
                showToast("Erro ao buscar comunidade no Firebase")
            }
    }


    private fun setListeners() {
        binding.imageSignOut.setOnClickListener { signOut() }
    }

    private fun init() {
        communities = mutableListOf()
        communityAdapter = CommunityAdapter(apiCommunityList, this)
        binding.conversationsRecyclerView.adapter = communityAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
