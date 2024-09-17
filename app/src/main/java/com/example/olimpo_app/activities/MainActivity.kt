package com.example.olimpo_app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.olimpo_app.databinding.ActivityMainBinding
import com.example.olimpo_app.listeners.ConversionListener
import com.example.olimpo_app.models.User
import com.example.olimpo_app.utilites.Constants
import com.example.olimpo_app.utilites.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity(), ConversionListener {

    private lateinit var bindind: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindind.root)
        preferenceManager = PreferenceManager(applicationContext)

        getToken()
        setListeners()
//        listenConversations()

        bindind.encontrarComunidade.setOnClickListener{
            val intent = Intent(applicationContext, FindCommunitiesActivity::class.java)
            startActivity(intent)
        }
        bindind.imageSolicitation.setOnClickListener{
            val intent = Intent(applicationContext, SolicitacaoActivity::class.java)
            startActivity(intent)
        }
        bindind.btnCreateCommunity.setOnClickListener{
            val intent = Intent(applicationContext, CreateCommunityActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setListeners(){
        bindind.imageSignOut.setOnClickListener { signOut() }
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

//    private fun listenConversations() {
//        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
//            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
//            .addSnapshotListener(eventListener)
//        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
//            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
//            .addSnapshotListener(eventListener)
//    }
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
                startActivity(Intent(applicationContext,LoginActivity::class.java))
                finish()
            }
            ?.addOnFailureListener { showToast("Unable to sign out") }

    }

    override fun onConversionClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }
}