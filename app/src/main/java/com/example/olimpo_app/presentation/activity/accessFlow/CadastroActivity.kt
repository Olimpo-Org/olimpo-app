package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import java.io.ByteArrayOutputStream
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

    private fun setListeners(){
        binding.textLogin.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.buttonSignIn.setOnClickListener{
            if (isValidSignUpDetails()){
                signUpFirebaseAndApi() // Chama os dois métodos de cadastro
            }
        }
        binding.fotoPerfil.setOnClickListener {
            val galeriaIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galeriaIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(galeriaIntent)
        }
    }

    private fun signUpFirebaseAndApi() {
        loading(true)
        if (encodedImage != null) {
            lifecycleScope.launch {
                val imageUrl = imageUpload.uploadImage(encodedImage!!)
                if (imageUrl != null) {
                    signUpFirebase(imageUrl) {
                        singUpApi(imageUrl).let {
                            loading(false)
                        }
                    }
                } else {
                    loading(false)
                    showToast("Falha no upload da imagem")
                }

            }
        }
    }

    private fun signUpFirebase(imageUrl: String, onSuccess: () -> Unit) {
        val database = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            Constants.KEY_NAME to binding.inputName.text.toString(),
            Constants.KEY_EMAIL to binding.inputEmail.text.toString(),
            Constants.KEY_PASSWORD to binding.inputPassword.text.toString(),
            Constants.KEY_IMAGE to imageUrl
        )
        database.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener {
                preferenceManager.putString(Constants.KEY_FIREBASE_USER_ID, it.id)
                onSuccess()
            }
            .addOnFailureListener { e ->
                e.message?.let { message -> showToast(message) }
                loading(false)
                ErrorLog().log(
                    "CadastroActivity",
                    e
                )
            }
    }

    private fun singUpApi(imageUrl: String) {
        lifecycleScope.launch {
            try {
                val user = UserAPI(
                    null,
                    binding.inputEmail.text.toString(),
                    binding.inputPassword.text.toString(),
                    binding.inputName.text.toString(),
                    binding.inputSurname?.text.toString(),
                    binding.inputCpf?.text.toString(),
                    imageUrl,
                    if (binding.genero?.text == "masculino") 1 else 2,
                )
                Log.d(
                    "CadastroActivity",
                    user.toString()
                )
                val response = accessRepository.createUser(user)
                response.toString()
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.id?.let {
                        preferenceManager.putLong(Constants.KEY_API_USER_ID, it)
                    }
                    preferenceManager.putString(Constants.KEY_NAME, response.body()!!.name)
                    preferenceManager.putString(Constants.KEY_IMAGE, response.body()!!.profileImage)
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    jsonConverter.saveObjectToJson(this@CadastroActivity, Constants.KEY_OBJ_USER, response.body()!!)
                    Log.d("CadastroActivity", "User: ${response.body()!!}")
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    loading(false)
                    showToast("Tente novamente")
                    Log.d(
                        "CadastroActivity",
                        "Erro: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                loading(false)
                showToast("Tente novamente")
                ErrorLog().log(
                    "CadastroActivity",
                    e
                )
            }
        }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            if (it.data != null) {
                val imageUri = it.data?.data
                try {
                    val inputStream = imageUri?.let { uri -> contentResolver.openInputStream(uri) }
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.fotoPerfil.setImageBitmap(bitmap)
                    encodedImage = bitmap
                } catch (e: FileNotFoundException) {
                    ErrorLog().log(
                        "CadastroActivity",
                        e
                    )
                }
            }
        }
    }

    // Validation
    private fun isValidSignUpDetails(): Boolean {
        if(binding.inputName.text.toString().trim().isEmpty() ||
            binding.inputEmail.text.toString().trim().isEmpty() ||
            binding.inputPassword.text.toString().trim().isEmpty()) {
            binding.textNome.setTextColor(getColor(R.color.Red_Bad))
            binding.textEmail.setTextColor(getColor(R.color.Red_Bad))
            binding.textSenha.setTextColor(getColor(R.color.Red_Bad))
            binding.errorMessage.visibility = View.VISIBLE
            return false
        } else if (encodedImage == null) {
            binding.errorMessage.visibility = View.VISIBLE
            showToast("Insira uma foto de perfil")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            binding.textEmail.setTextColor(getColor(R.color.Red_Bad))
            binding.errorMessage.visibility = View.VISIBLE
            return false
        } else {
            binding.textNome.setTextColor(getColor(R.color.Blue))
            binding.textEmail.setTextColor(getColor(R.color.Blue))
            binding.textSenha.setTextColor(getColor(R.color.Blue))
            binding.errorMessage.visibility = View.GONE
            return true
        }
    }

    // Utils
    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.buttonSignIn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignIn.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
