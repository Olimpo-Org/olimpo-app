package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.R
import com.example.olimpo_app.data.firebase.ImageUpload
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.repository.UserRepository
import com.example.olimpo_app.databinding.ActivityCadastroBinding
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ErrorLog
import com.example.olimpo_app.utils.JsonConverter
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private lateinit var preferenceManager: PreferenceManager
    private var encodedImage: Bitmap? = null
    private val accessRepository = UserRepository(AccessApiInstance.service)
    private val imageUpload = ImageUpload()
    private val jsonConverter = JsonConverter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(applicationContext)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.textLogin.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.buttonSignIn.setOnClickListener { if (isValidSignUpDetails()) signUpFirebaseAndApi() }
        binding.fotoPerfil.setOnClickListener { openGallery() }
    }

    private fun signUpFirebaseAndApi() {
        if (encodedImage != null) {
            lifecycleScope.launch {
                loading(true)
                val imageUrl = imageUpload.uploadImage(encodedImage!!)
                if (imageUrl != null) {
                    registerUser(imageUrl)
                } else {
                    showToast("Falha no upload da imagem")
                    loading(false)
                }
            }
        }
    }

    private suspend fun registerUser(imageUrl: String) {
        signUpApi(imageUrl) { userId ->
            signUpFirebase(imageUrl, userId) {
                navigateToMainActivity()
            }
        }
    }

    private fun signUpFirebase(imageUrl: String, userId: String, onSuccess: () -> Unit) {
        val user = createUserHashMap(imageUrl, userId)
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener {
                preferenceManager.putString(Constants.KEY_FIREBASE_USER_ID, it.id)
                onSuccess()
            }
            .addOnFailureListener { e ->
                handleFailure(e, "Falha ao cadastrar no Firebase")
            }
    }

    private fun createUserHashMap(imageUrl: String, userId: String) = hashMapOf(
        Constants.KEY_NAME to binding.inputName.text.toString(),
        Constants.KEY_EMAIL to binding.inputEmail.text.toString(),
        Constants.KEY_PASSWORD to binding.inputPassword.text.toString(),
        Constants.KEY_IMAGE to imageUrl,
        Constants.KEY_USER_API_ID_IN_FIREBASE to userId
    )

    private suspend fun signUpApi(imageUrl: String, onSuccess: (String) -> Unit) {
        try {
            val user = createUser(imageUrl)
            val response = accessRepository.createUser(user)
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { handleSuccessfulApiResponse(it, onSuccess) }
            } else {
                showToast("Tente novamente")
                logError("Erro ao criar usuário na API", response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleFailure(e, "Erro ao criar usuário na API")
        }
    }

    private fun createUser(imageUrl: String) = UserAPI(
        id = null,
        email = binding.inputEmail.text.toString(),
        password = binding.inputPassword.text.toString(),
        name = binding.inputName.text.toString(),
        surname = binding.inputSurname?.text.toString(),
        cpf = binding.inputCpf?.text.toString(),
        profileImage = imageUrl,
        genderId = if (binding.genero?.text == "masculino") 1 else 2,
    )

    private fun handleSuccessfulApiResponse(responseBody: UserAPI, onSuccess: (String) -> Unit) {
        jsonConverter.saveObjectToJson(this, Constants.KEY_OBJ_USER, responseBody)
        onSuccess(responseBody.id.toString())
    }

    private fun openGallery() {
        val galeriaIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galeriaIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickImage.launch(galeriaIntent)
    }

    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data?.data != null) {
            try {
                val bitmap = contentResolver.openInputStream(result.data!!.data!!)?.let { BitmapFactory.decodeStream(it) }
                bitmap?.let {
                    binding.fotoPerfil.setImageBitmap(it)
                    encodedImage = it
                }
            } catch (e: FileNotFoundException) {
                handleFailure(e, "Imagem não encontrada")
            }
        }
    }

    private fun isValidSignUpDetails(): Boolean {
        return when {
            binding.inputName.text.isNullOrEmpty() ||
                    binding.inputEmail.text.isNullOrEmpty() ||
                    binding.inputPassword.text.isNullOrEmpty() -> {
                showErrorMessage(R.color.Red_Bad, "Preencha todos os campos")
                false
            }
            encodedImage == null -> {
                showErrorMessage(R.color.Red_Bad, "Insira uma foto de perfil")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches() -> {
                showErrorMessage(R.color.Red_Bad, "Email inválido")
                false
            }
            else -> {
                binding.errorMessage.visibility = View.GONE
                true
            }
        }
    }

    private fun showErrorMessage(color: Int, message: String) {
        binding.textNome.setTextColor(getColor(color))
        binding.textEmail.setTextColor(getColor(color))
        binding.textSenha.setTextColor(getColor(color))
        binding.errorMessage.text = message
        binding.errorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean) {
        binding.buttonSignIn.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleFailure(e: Exception, message: String) {
        showToast(message)
        ErrorLog().log("CadastroActivity", e)
        loading(false)
    }

    private fun navigateToMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(this)
        }
    }

    private fun logError(tag: String, message: String?) {
        Log.e(tag, message.orEmpty())
    }
}
