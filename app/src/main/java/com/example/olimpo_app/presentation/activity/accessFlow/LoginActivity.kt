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
import com.example.olimpo_app.utils.JsonConverter
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceManager: PreferenceManager
    private val accessRepository = UserRepository(AccessApiInstance.service)
    private val jsonConverter = JsonConverter()

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

    private fun signInFirebase() {
        Log.d("LoginActivity", "Iniciando login no Firebase")
        val database = FirebaseFirestore.getInstance()

        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.text.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                    Log.d("LoginActivity", "Login no Firebase bem-sucedido")
                    val documentSnapshot = task.result!!.documents[0]
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(Constants.KEY_FIREBASE_USER_ID, documentSnapshot.id)
                    preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME)!!)
                    preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE)!!)
                } else {
                    Log.d("LoginActivity", "Falha no login com Firebase")
                    loading(false)
                    showToast("Não foi possível logar com Firebase")
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Erro no Firebase: ${e.message}")
                loading(false)
                showToast("Erro ao tentar logar no Firebase")
            }
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

                    response.body()!!.id?.let {
                        preferenceManager.putLong(
                            Constants.KEY_API_USER_ID,
                            it
                        )
                    }
                    preferenceManager.putString(Constants.KEY_NAME, response.body()!!.name)
                    preferenceManager.putString(Constants.KEY_IMAGE, response.body()!!.profileImage)
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    jsonConverter.saveObjectToJson(this@LoginActivity, Constants.KEY_OBJ_USER, response.body()!!)

                    // Redireciona para a MainActivity após o login
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
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
            startActivity(Intent(this, CadastroActivity::class.java))
            binding.textEmail.setTextColor(getColor(R.color.Blue))
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.errorMessage.visibility = View.GONE
        }

        binding.buttonSignIn.setOnClickListener {
            if (isValidSignInDetails()) {
                loading(true)
                Log.d("LoginActivity", "Iniciando processo de login")
                signInFirebase()
                signInApi()
                loading(false)

            }
        }
    }

    // Função para mostrar ou esconder o carregamento
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignIn.visibility = View.INVISIBLE
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
            binding.textSenha.setTextColor(getColor(R.color.Red_Bad))
            binding.errorMessage.visibility = View.VISIBLE
            return false

        } else if (binding.inputEmail.text.toString().trim().isEmpty()) {
            Log.d("LoginActivity", "E-mail está vazio")
            binding.textEmail.setTextColor(getColor(R.color.Red_Bad))
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.errorMessage.visibility = View.VISIBLE
            return false

        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            Log.d("LoginActivity", "E-mail inválido")
            binding.textEmail.setTextColor(getColor(R.color.Red_Bad))
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.errorMessage.visibility = View.VISIBLE
            return false

        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            Log.d("LoginActivity", "Senha está vazia")
            binding.textEmail.setTextColor(getColor(R.color.Blue))
            binding.textSenha.setTextColor(getColor(R.color.Red_Bad))
            binding.errorMessage.visibility = View.VISIBLE
            return false

        } else {
            Log.d("LoginActivity", "Detalhes de login válidos")
            binding.textEmail.setTextColor(getColor(R.color.Blue))
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.errorMessage.visibility = View.GONE
            return true
        }
    }
}
