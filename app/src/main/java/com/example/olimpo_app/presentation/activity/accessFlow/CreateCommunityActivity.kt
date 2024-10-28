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
import com.example.olimpo_app.data.repository.CommunityRepository
import com.example.olimpo_app.databinding.ActivityCriarComunidadesBinding
import com.example.olimpo_app.presentation.activity.feedFlow.HomeActivity
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.sql.Date

class CreateCommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriarComunidadesBinding
    private lateinit var preferenceManager: PreferenceManager
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
        binding.btnCreateCommunity.setOnClickListener { createCommunity() }
        binding.fotoPerfil.setOnClickListener { openGallery() }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private fun navigateToSolicitationActivity() {
        startActivity(Intent(applicationContext, SolicitacaoActivity::class.java))
        finish()
    }

    // Processa a criação de uma nova comunidade
    private fun createCommunity() {
        if (isValidCreateDetails()) {
            lifecycleScope.launch {
                val imageUrl = imageUpload.uploadImage(image!!)
                imageUrl?.let {
                    createCommunityApi(it) { communityApiId ->
                        createCommunityFirebase(it, communityApiId) {
                            showToast("Comunidade criada com sucesso")
                            startActivity(Intent(applicationContext, HomeActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            })
                        }
                    }
                }
            }
        }
    }

    private fun createCommunityApi(imageUrl: String, onSuccess: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val community = CommunityAPI(
                    id = null,
                    name = binding.inputName.text.toString(),
                    startDate = Date(System.currentTimeMillis()),
                    neighborhood = binding.inputNeighborhood.text.toString(),
                    imageUrl = imageUrl
                )
                val response = communityRepository.createCommunity(community)
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let { onSuccess(it.id.toString()) }
                } else {
                    showError("Erro ao criar comunidade na API")
                }
            } catch (e: Exception) {
                showError("Erro ao criar comunidade. Tente novamente")
            }
        }
    }

    private fun createCommunityFirebase(imageUrl: String, communityApiId: String, onSuccess: () -> Unit) {
        val community = mapOf(
            Constants.KEY_COMMUNITY_NAME to binding.inputName.text.toString(),
            Constants.KEY_COMMUNITY_IMAGE to image!!,
            Constants.KEY_COMMUNITY_API_ID to communityApiId
        )

        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_COMMUNITY)
            .add(community)
            .addOnSuccessListener { communityDocument ->
                saveCommunityLocally(communityDocument.id, imageUrl)
                addUserToCommunity(communityDocument.id)
                onSuccess()
            }
            .addOnFailureListener { e -> showError(e.message ?: "Erro ao criar comunidade no Firebase") }
    }

    private fun saveCommunityLocally(communityId: String, imageUrl: String) {
        preferenceManager.apply {
            putBoolean(Constants.KEY_IS_CREATE, true)
            putString(Constants.KEY_COMMUNITY_ID, communityId)
            putString(Constants.KEY_COMMUNITY_NAME, binding.inputName.text.toString())
            putString(Constants.KEY_COMMUNITY_IMAGE, imageUrl)
        }
    }

    private fun addUserToCommunity(communityId: String) {
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.find { it.id == preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID) }
                    ?.let { userDocument ->
                        FirebaseFirestore.getInstance().batch().apply {
                            update(
                                FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_COMMUNITY)
                                    .document(communityId),
                                mapOf(Constants.KEY_COMMUNITY_MEMBERS to FieldValue.arrayUnion(userDocument))
                            ).commit()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) showToast("Usuário adicionado à comunidade.")
                                    else showToast("Falha ao adicionar usuário à comunidade.")
                                }
                        }
                    } ?: showToast("Usuário não encontrado.")
            }
            .addOnFailureListener { e -> showError("Erro ao buscar usuários: ${e.message}") }
    }

    private fun signOut() {
        showToast("Saindo...")
        preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID)?.let {
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                .document(it)
                .update(mapOf(Constants.KEY_COMMUNITY_TOKEN to FieldValue.delete()))
                .addOnSuccessListener {
                    preferenceManager.clear()
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                }
                .addOnFailureListener { showToast("Não foi possível sair :/") }
        }
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
                binding.textName.setTextColor(getColor(R.color.Red_Bad))
                binding.errorMessage.visibility = View.VISIBLE
                false
            }
            image == null -> {
                binding.errorMessage.visibility = View.VISIBLE
                showToast("Insira uma foto de perfil")
                false
            }
            else -> {
                binding.textName.setTextColor(getColor(R.color.Blue))
                binding.errorMessage.visibility = View.GONE
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
