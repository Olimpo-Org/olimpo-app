package com.example.olimpo_app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.databinding.ActivityLoginBinding
import com.example.olimpo_app.utilites.Constants
import com.example.olimpo_app.utilites.PreferenceManager

class login_activity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext)
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }
    private fun signIn() {
        loading(true)
        val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
    }

        private fun setListeners(){
            binding.textCreateNewAccount.setOnClickListener {
                startActivity(Intent(this, cadastro_activity::class.java))
            }
            binding.buttonSignIn.setOnClickListener {
                if (isValidSignInDetails()) {
                    signIn()
                }
            }
        }

        private fun loading(isLoading: Boolean){
            if(isLoading){
                binding.buttonSignIn.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.INVISIBLE
                binding.buttonSignIn.visibility = View.VISIBLE
            }
        }

        private fun showToast(message: String){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        private fun isValidSignInDetails(): Boolean {
            if(binding.inputEmail.text.toString().trim().isEmpty()){
                showToast("Insira um email")
                return false
            }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
                showToast("Insira um email válido")
                return false
            }else if(binding.inputPassword.text.toString().trim().isEmpty()) {
                showToast("Insira uma senha")
                return false
            }else{
                return true
            }
    }
}