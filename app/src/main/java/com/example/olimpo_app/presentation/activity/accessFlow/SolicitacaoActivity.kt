package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.databinding.ActivitySolicitacaoBinding
import com.example.olimpo_app.presentation.activity.BaseActivity
import com.example.olimpo_app.presentation.activity.feedFlow.MainActivity
import com.example.olimpo_app.presentation.listeners.UserListener
import com.example.olimpo_app.presentation.adapters.AcceptUsersAdapter
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SolicitacaoActivity : BaseActivity(), UserListener {
    private lateinit var binding: ActivitySolicitacaoBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolicitacaoBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(applicationContext)
        setContentView(binding.root)


        setListeners()
        getUsers()

        binding.buttonArrow.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageSolicitation.setOnClickListener{
            val intent = Intent(applicationContext, SolicitacaoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun getUsers(){
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener {
                val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                if (it.isSuccessful && it.result != null){
                    val users = mutableListOf<User>()
                    for(queryDocumentSnapshot in it.result){
                        if(currentUserId.equals(queryDocumentSnapshot.id)){
                            continue
                        }
                        val user = User(
                            name = queryDocumentSnapshot.getString(Constants.KEY_NAME)!!,
                            image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)!!,
                            email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)!!,
                            token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN),
                            id = queryDocumentSnapshot.id
                        )
                        users.add(user)
                    }
                    if (users.size > 0){
                        val acceptUsersAdapter = AcceptUsersAdapter(users, this)
                        binding.UsersRecyclerView.adapter = acceptUsersAdapter
                        binding.UsersRecyclerView.visibility = View.VISIBLE
                    }else{

                    }
                }else{

                }
            }
    }
    private fun setListeners(){
        binding.buttonSignOut.setOnClickListener { signOut() }
    }
    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
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
}