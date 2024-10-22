package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.olimpo_app.data.model.accessFlow.Community
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.databinding.ActivityMainBinding
import com.example.olimpo_app.presentation.activity.BaseActivity
import com.example.olimpo_app.presentation.activity.feedFlow.HomeActivity
import com.example.olimpo_app.presentation.activity.messageFlow.ChatActivity
import com.example.olimpo_app.presentation.listeners.CommunityListener
import com.example.olimpo_app.presentation.listeners.ConversionListener
import com.example.olimpo_app.presentation.adapters.CommunityListUniAdapter
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity(), ConversionListener, CommunityListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var communityListUniAdapter: CommunityListUniAdapter
    private lateinit var communities: MutableList<Community>
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)

        init()
        getToken()
        setListeners()
        listenConversations()

        binding.encontrarComunidade.setOnClickListener{
            val intent = Intent(applicationContext, FindCommunitiesActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageSolicitation.setOnClickListener{
            val intent = Intent(applicationContext, SolicitacaoActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnCreateCommunity.setOnClickListener{
            val intent = Intent(applicationContext, CreateCommunityActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun setListeners(){
        binding.imageSignOut.setOnClickListener { signOut() }
    }
    private fun init() {
        communities = ArrayList()
        communityListUniAdapter = CommunityListUniAdapter(communities, this)
        binding.conversationsRecyclerView.adapter = communityListUniAdapter
        database = FirebaseFirestore.getInstance()
    }
    private fun showToast(text: String){
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
            communityListUniAdapter.notifyDataSetChanged()
            binding.conversationsRecyclerView.visibility = if (communities.isNotEmpty()) View.VISIBLE else View.GONE
            binding.LinearLayout.visibility = View.GONE
        } else {
            // Se não houver dados, esconde o RecyclerView
            binding.conversationsRecyclerView.visibility = View.GONE
            binding.LinearLayout.visibility = View.VISIBLE
        }
    }
    private fun getToken(){
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { updateToken(it) }
    }

    private fun updateToken(token: String){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token)
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USER_ID)!!)
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener { showToast("Indisponível para atualizar o token") }

    }

    private fun signOut() {
        showToast("Saindo...")
        val database = FirebaseFirestore.getInstance()
        val documentReference = preferenceManager.getString(Constants.KEY_USER_ID)?.let {
            database.collection(Constants.KEY_COLLECTION_USERS).document(it)
        }
        val updates = hashMapOf<String, Any>( Constants.KEY_FCM_TOKEN to FieldValue.delete() )
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
    override fun onCommunityClicked(community: Community) {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra(Constants.KEY_COLLECTION_COMMUNITY, community.toString())
        startActivity(intent)
    }
}