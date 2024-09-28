package com.example.olimpo_app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.databinding.ActivityFindCommunitiesBinding
import com.example.olimpo_app.utilites.Constants
import com.example.olimpo_app.utilites.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FindCommunitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindCommunitiesBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindCommunitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()

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
                startActivity(Intent(applicationContext,LoginActivity::class.java))
                finish()
            }
            ?.addOnFailureListener { showToast("Não foi possível sair :/") }

    }
}