package com.example.olimpo_app.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.databinding.ActivityMainBinding
import com.example.olimpo_app.utilites.Constants
import com.example.olimpo_app.utilites.PreferenceManager

class MainActivity : AppCompatActivity() {
    private lateinit var bindind: ActivityMainBinding;
    private lateinit var preferenceManager: PreferenceManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindind.root)

        loadUserDetails()
//        setListeners()
    }
//    private fun setListeners(){
//        bindind.imageSignOut.setOnClickListener { signOut() }
//    }


    private fun loadUserDetails(){
//        bindind.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        bindind.imageProfile.setImageBitmap(bitmap)
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }



    private fun signOut() {
        showToast("Saindo...")
                startActivity(Intent(applicationContext, login_activity::class.java))
                finish()
    }
}