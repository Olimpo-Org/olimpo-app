package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.Login
import com.example.olimpo_app.data.repository.UserRepository
import com.example.olimpo_app.databinding.ActivityLoginBinding
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import com.example.olimpo_app.utils.PreferenceManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceManager: PreferenceManager
    private val accessRepository = UserRepository(AccessApiInstance.service)
    private val objectsLocalStorage = ObjectsLocalStorage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext)

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun signInApi() {
        try {
            Log.d("LoginActivity", "Iniciando login via API")

            lifecycleScope.launch {
                val login = Login(
                    binding.inputEmail.text.toString(),
                    binding.inputPassword.text.toString()
                )
                val response = accessRepository.login(login)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("LoginActivity", "Login via API bem-sucedido")
                    response.body()!!.let {
                        objectsLocalStorage.saveObjectInLocalStorage(
                            this@LoginActivity,
                            Constants.KEY_OBJ_USER_API,
                            it
                        )
                        preferenceManager.putString(
                            Constants.KEY_API_USER_ID,
                            it.id.toString()
                        )
                        preferenceManager.putString(Constants.KEY_NAME,
                            it.name.toString()
                        )
                        preferenceManager.putString(Constants.KEY_IMAGE,
                            it.profileImage.toString()
                        )
                    }
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    objectsLocalStorage.saveObjectInLocalStorage(this@LoginActivity, Constants.KEY_OBJ_USER, response.body()!!)

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("LoginActivity", "Falha ao logar via API: ${response.code()}")
                    loading(false)
                    showToast("Falha ao logar via API")
                }
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Erro durante login via API: ${e.message}")
            loading(false)
            showToast("Erro ao tentar logar via API")
        }
    }

    // Configura listeners dos botões
    private fun setListeners() {
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            binding.textEmail.setTextColor(getColor(R.color.Blue))
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.errorMessage.visibility = View.GONE
        }

        binding.buttonSignIn.setOnClickListener {
            loading(true)
            if (isValidSignInDetails()) {
                Log.d("LoginActivity", "Iniciando processo de login")
                signInApi()
                loading(false)
            }
        }
    }

    // Função para mostrar ou esconder o carregamento
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignIn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignIn.visibility = View.VISIBLE
        }
    }

    // Exibe mensagens de erro ou sucesso
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Valida os detalhes de login
    private fun isValidSignInDetails(): Boolean {
        if (binding.inputEmail.text.toString().trim().isEmpty() &&
            binding.inputPassword.text.toString().trim().isEmpty()) {
            Log.d("LoginActivity", "E-mail e senha estão vazios")
            binding.textEmail.setTextColor(getColor(R.color.Red_Bad))
            binding.inputEmail.setBackgroundResource(R.drawable.borda_vermelha)
            binding.textSenha.setTextColor(getColor(R.color.Red_Bad))
            binding.inputPassword.setBackgroundResource(R.drawable.borda_vermelha)
            binding.errorMessage.visibility = View.VISIBLE
            return false

        } else if (binding.inputEmail.text.toString().trim().isEmpty()) {
            Log.d("LoginActivity", "E-mail está vazio")
            binding.textEmail.setTextColor(getColor(R.color.Red_Bad))
            binding.inputEmail.setBackgroundResource(R.drawable.borda_vermelha)
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.inputPassword.setBackgroundResource(R.drawable.borda_cinza)
            binding.errorMessage.visibility = View.VISIBLE
            return false

        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            Log.d("LoginActivity", "E-mail inválido")
            binding.textEmail.setTextColor(getColor(R.color.Red_Bad))
            binding.inputEmail.setBackgroundResource(R.drawable.borda_vermelha)
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.inputPassword.setBackgroundResource(R.drawable.borda_cinza)
            binding.errorMessage.visibility = View.VISIBLE
            return false

        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            Log.d("LoginActivity", "Senha está vazia")
            binding.textEmail.setTextColor(getColor(R.color.Blue))
            binding.inputEmail.setBackgroundResource(R.drawable.borda_cinza)
            binding.textSenha.setTextColor(getColor(R.color.Red_Bad))
            binding.inputPassword.setBackgroundResource(R.drawable.borda_vermelha)
            binding.errorMessage.visibility = View.VISIBLE
            return false

        } else {
            Log.d("LoginActivity", "Detalhes de login válidos")
            binding.textEmail.setTextColor(getColor(R.color.Blue))
            binding.inputEmail.setBackgroundResource(R.drawable.borda_cinza)
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.inputPassword.setBackgroundResource(R.drawable.borda_cinza)
            binding.errorMessage.visibility = View.GONE
            return true
        }
    }
}
