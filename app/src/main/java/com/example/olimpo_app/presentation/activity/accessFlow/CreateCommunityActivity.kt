package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.R
import com.example.olimpo_app.data.firebase.ImageUpload
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.repository.CommunityRepository
import com.example.olimpo_app.databinding.ActivityCriarComunidadesBinding
import com.example.olimpo_app.presentation.activity.feedFlow.HomeActivity
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import com.example.olimpo_app.utils.PreferenceManager
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class CreateCommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriarComunidadesBinding
    private lateinit var preferenceManager: PreferenceManager
    private val objectLocalStorage = ObjectsLocalStorage()
    private var image: Bitmap? = null
    private val communityRepository = CommunityRepository(AccessApiInstance.service)
    private val imageUpload = ImageUpload()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarComunidadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)
        initializeUI()
    }

    // Configura os listeners iniciais
    private fun initializeUI() {
        binding.buttonArrow.setOnClickListener { navigateToMainActivity() }
        binding.imageSolicitation.setOnClickListener { navigateToSolicitationActivity() }
        binding.buttonSignOut.setOnClickListener { signOut() }
        binding.btnCreateCommunity.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            createCommunity() }
        binding.fotoPerfil.setOnClickListener { openGallery() }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private fun navigateToSolicitationActivity() {
        startActivity(Intent(applicationContext, SolicitationActivity::class.java))
        finish()
    }

    // Processa a criação de uma nova comunidade
    private fun createCommunity() {
        if (isValidCreateDetails()) {
            lifecycleScope.launch {
                val imageUrl = imageUpload.uploadImage(image!!)
                imageUrl?.let {
                    createCommunityApi(it) { communityApiId ->
                        showToast("Comunidade criada com sucesso")
                        saveCommunityLocally(communityApiId, it)
                        startActivity(Intent(applicationContext, HomeActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        })
                    }
                }
            }
        }
    }

    private fun createCommunityApi(imageUrl: String, onSuccess: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(System.currentTimeMillis()))
                val community = CommunityAPI(
                    id = null,
                    name = binding.inputName.text.toString(),
                    startDate = formattedDate,
                    neighborhood = binding.inputNeighborhood!!.text.toString(),
                    imageUrl = imageUrl
                )
                val user = objectLocalStorage.getObjectFromLocalStorage(this@CreateCommunityActivity, Constants.KEY_OBJ_USER_API, UserAPI::class.java)
                val response = user?.id?.let {
                    communityRepository.createCommunity(
                        community,
                        it
                    )
                }
                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.let { onSuccess(it.id.toString()) }
                    } else {
                        showError("Erro ao criar comunidade na API: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("CreateCommunityError", "Erro ao criar comunidade na API", e)
            }
        }
    }

    private fun saveCommunityLocally(communityId: String, imageUrl: String) {
        preferenceManager.apply {
            putBoolean(Constants.KEY_IS_CREATE, true)
            putString(Constants.KEY_COMMUNITY_ID, communityId)
            putString(Constants.KEY_COMMUNITY_NAME, binding.inputName.text.toString())
            putString(Constants.KEY_COMMUNITY_IMAGE, imageUrl)
        }
    }

    private fun signOut() {
        showToast("Saindo...")
        preferenceManager.clear()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    private fun openGallery() {
        val galeriaIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galeriaIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickImage.launch(galeriaIntent)
    }

    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            try {
                result.data?.data?.let { uri ->
                    contentResolver.openInputStream(uri)?.let { inputStream ->
                        BitmapFactory.decodeStream(inputStream).also { bitmap ->
                            binding.fotoPerfil.setImageBitmap(bitmap)
                            image = bitmap
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun isValidCreateDetails(): Boolean {
        return when {
            binding.inputName.text.toString().trim().isEmpty() -> {
                binding.inputName.setBackgroundResource(R.drawable.borda_vermelha)
                false
            }
            image == null -> {
                showToast("Insira uma foto de perfil")
                false
            }
            else -> {
                binding.inputName.setBackgroundResource(R.drawable.borda_cinza)
                true
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun showError(message: String) {
        Log.d("Erro", message)
        showToast("Tente novamente")
    }
}
