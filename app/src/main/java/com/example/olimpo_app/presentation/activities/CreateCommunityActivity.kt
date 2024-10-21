package com.example.olimpo_app.presentation.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.olimpo_app.R
import com.example.olimpo_app.databinding.ActivityCriarComunidadesBinding
import com.example.olimpo_app.utilites.Constants
import com.example.olimpo_app.utilites.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class CreateCommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCriarComunidadesBinding
    private lateinit var preferenceManager: PreferenceManager
    private var encodedImage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarComunidadesBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(applicationContext)
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
        binding.btnCreateCommunity.setOnClickListener{
            if (isValidCreateDetails()) {
                createCommunity()
            }
        }

    binding.fotoPerfil.setOnClickListener {
        val galeriaIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galeriaIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickImage.launch(galeriaIntent)
        }
    }
    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
    private fun createCommunity() {
        val database = FirebaseFirestore.getInstance()

        // Criação da comunidade
        val community = hashMapOf(
            Constants.KEY_COMMUNITY_NAME to binding.inputName.text.toString(),
            Constants.KEY_COMMUNITY_IMAGE to encodedImage!!
        )

        // Adiciona a nova comunidade no Firestore
        database.collection(Constants.KEY_COLLECTION_COMMUNITY)
            .add(community)
            .addOnSuccessListener { communityDocument ->  // Retorna o documento criado
                val communityId = communityDocument.id
                preferenceManager.putBoolean(Constants.KEY_IS_CREATE, true)
                preferenceManager.putString(Constants.KEY_COMMUNITY_ID, communityId)
                preferenceManager.putString(Constants.KEY_COMMUNITY_NAME, binding.inputName.text.toString())
                preferenceManager.putString(Constants.KEY_COMMUNITY_IMAGE, encodedImage!!)

                // Chama o método para adicionar todos os usuários à comunidade recém-criada
                addUsersToCommunity(communityId)

                // Redireciona para a HomeActivity
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                e.message?.let { message -> showToast(message) }
            }
    }

    private fun addUsersToCommunity(communityId: String) {
        val database = FirebaseFirestore.getInstance()

        // Busca todos os usuários da coleção de usuários
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = database.batch()

                // Lista de IDs dos usuários
                val userIds = mutableListOf<String>()

                for (document in querySnapshot.documents) {
                    val userId = document.id
                    userIds.add(userId)
                }

                val communityData = hashMapOf(
                    Constants.KEY_COMMUNITY_MEMBERS to userIds  // Lista de IDs de usuários
                )

                // Atualiza o documento da comunidade com a lista de membros
                val communityRef = database.collection(Constants.KEY_COLLECTION_COMMUNITY).document(communityId)
                batch.update(communityRef, communityData as Map<String, Any>)

                // Executa o batch de operações
                batch.commit().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Todos os usuários foram adicionados à comunidade.")
                    } else {
                        showToast("Falha ao adicionar usuários à comunidade.")
                    }
                }
            }
            .addOnFailureListener { e ->
                showToast("Erro ao buscar usuários: ${e.message}")
            }
    }

    private fun signOut() {
        showToast("Saindo...")
        val database = FirebaseFirestore.getInstance()
        val documentReference = preferenceManager.getString(Constants.KEY_USER_ID)?.let {
            database.collection(Constants.KEY_COLLECTION_USERS).document(it)
        }
        val updates = hashMapOf<String, Any>( Constants.KEY_COMMUNITY_TOKEN to FieldValue.delete() )
        documentReference?.update(updates)
            ?.addOnSuccessListener {
                preferenceManager.clear()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
            ?.addOnFailureListener { showToast("Não foi possível sair :/") }

    }

    private fun encodeImage(bitmap: Bitmap): String{
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        if (it.resultCode == RESULT_OK) {
            if (it.data != null) {
                val imageUri = it.data?.data
                try {
                    val inputStream = imageUri?.let { uri -> contentResolver.openInputStream(uri) }
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.fotoPerfil.setImageBitmap(bitmap)
                    encodedImage = encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
        private fun isValidCreateDetails(): Boolean {
    if(binding.inputName.text.toString().trim().isEmpty()){
    binding.textName.setTextColor(getColor(R.color.Red_Bad))
    binding.errorMessage.visibility = View.VISIBLE
    return false
    }else if(encodedImage == null){
    binding.errorMessage.visibility = View.VISIBLE
    showToast("Insira uma foto de perfil")
    return false
    }else {
        binding.textName.setTextColor(getColor(R.color.Blue))
        binding.errorMessage.visibility = View.GONE
        return true
        }
    }
}