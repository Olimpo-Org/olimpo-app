package com.example.olimpo_app.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.data.models.Community
import com.example.olimpo_app.databinding.ActivityFindCommunitiesBinding
import com.example.olimpo_app.listeners.CommunityListener
import com.example.olimpo_app.presentation.adapters.CommunityListAdapter
import com.example.olimpo_app.utilites.Constants
import com.example.olimpo_app.utilites.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging

class FindCommunitiesActivity : AppCompatActivity(), CommunityListener {
    private lateinit var binding: ActivityFindCommunitiesBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var communityListAdapter: CommunityListAdapter
    private lateinit var communities: MutableList<Community>
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindCommunitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)

        init()
        getToken()
        setListeners()
        listenConversations()

        binding.buttonArrow.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageSolicitation.setOnClickListener {
            val intent = Intent(applicationContext, SolicitacaoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setListeners() {
        binding.buttonSignOut.setOnClickListener { signOut() }
    }

    private fun init() {
        communities = ArrayList()
        communityListAdapter = CommunityListAdapter(communities, this)
        binding.conversationsRecyclerView.adapter = communityListAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun listenConversations() {
        database.collection(Constants.KEY_COLLECTION_COMMUNITY)
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            // Log de erro ou notificação para o usuário
            showToast("Error fetching communities: ${error.message}")
            return@EventListener
        }
        if (value != null) {
            // Limpa a lista antes de adicionar novos dados
            communities.clear()
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val community = Community().apply {
                        id = documentChange.document.getString(Constants.KEY_COMMUNITY_ID) ?: ""
                        name = documentChange.document.getString(Constants.KEY_COMMUNITY_NAME) ?: ""
                        image = documentChange.document.getString(Constants.KEY_COMMUNITY_IMAGE) ?: ""
                    }
                    // Adiciona a comunidade na lista
                    communities.add(community)
                }
            }
            // Notifica o adaptador de uma vez só
            communityListAdapter.notifyDataSetChanged()
            binding.conversationsRecyclerView.visibility = if (communities.isNotEmpty()) View.VISIBLE else View.GONE
        } else {
            // Se não houver dados, esconde o RecyclerView
            binding.conversationsRecyclerView.visibility = View.GONE
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { updateToken(it) }
    }

    private fun updateToken(token: String) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token)
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USER_ID)!!)

        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener { showToast("Unable to update token") }
    }

    private fun signOut() {
        showToast("Saindo...")
        val database = FirebaseFirestore.getInstance()
        val documentReference = preferenceManager.getString(Constants.KEY_USER_ID)?.let {
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

    override fun onCommunityClicked(community: Community) {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra(Constants.KEY_COLLECTION_COMMUNITY, community.toString())
        startActivity(intent)
    }
}