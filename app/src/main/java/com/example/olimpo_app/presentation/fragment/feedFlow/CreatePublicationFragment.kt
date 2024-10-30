package com.example.olimpo_app.presentation.fragment.feedFlow

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olimpo_app.data.firebase.ImageUpload
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.repository.PublicationRepository
import com.example.olimpo_app.databinding.FragmentCreatePublicationBinding
import com.example.olimpo_app.presentation.adapters.SelectedImagesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CreatePublicationFragment(private val publicationRepository: PublicationRepository) : Fragment() {
    private lateinit var binding: FragmentCreatePublicationBinding
    private var selectedImages: MutableList<Bitmap> = mutableListOf() // Armazenando Bitmaps
    private val imageUpload = ImageUpload()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePublicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.btnPublish.setOnClickListener {
            val description = binding.editText.text.toString()
            if (description.isNotEmpty()) {
                createPublication(description)
            } else {
                Toast.makeText(requireContext(), "Digite uma descrição para a publicação", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.listImageRecycleView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = SelectedImagesAdapter(selectedImages.map { encodeBitmapToBase64(it) }.toMutableList()) // Exibir como Base64
        }
    }

    private fun createPublication(description: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Upload das imagens para o Firebase
                val imageUrls = imageUpload.uploadImageList(selectedImages)

                val publication = Publication(
                    publicationId = "",
                    communityId = "comunidade_id",
                    senderId = "usuario_id",
                    senderName = "nome_do_usuario",
                    images = imageUrls,
                    description = description,
                    likes = emptyList()
                )

                val response = withContext(Dispatchers.IO) {
                    publicationRepository.createPublication(publication)
                }

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Publicação criada com sucesso!", Toast.LENGTH_SHORT).show()
                    binding.editText.text.clear()
                    selectedImages.clear()
                    setupRecyclerView()
                } else {
                    Toast.makeText(requireContext(), "Erro ao criar publicação", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            data.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    val bitmap = loadBitmapFromUri(imageUri)
                    selectedImages.add(bitmap) // Armazenar Bitmap
                }
            } ?: data.data?.let { uri ->
                val bitmap = loadBitmapFromUri(uri)
                selectedImages.add(bitmap) // Armazenar Bitmap
            }
            setupRecyclerView()
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val contentResolver = requireContext().contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
}