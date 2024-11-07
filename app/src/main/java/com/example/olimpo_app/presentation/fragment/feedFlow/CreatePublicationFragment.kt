package com.example.olimpo_app.presentation.fragment.feedFlow

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olimpo_app.FeaturesApiInstance
import com.example.olimpo_app.R
import com.example.olimpo_app.data.firebase.ImageUpload
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import com.example.olimpo_app.data.repository.AnnoucementRepository
import com.example.olimpo_app.data.repository.PublicationRepository
import com.example.olimpo_app.databinding.FragmentCreatePublicationBinding
import com.example.olimpo_app.presentation.adapters.ImageAdapter
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.NotificationReceiver
import com.example.olimpo_app.utils.ObjectsLocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePublicationFragment : Fragment() {

    private lateinit var binding: FragmentCreatePublicationBinding
    private val imageAdapter = ImageAdapter()
    private var counterType = 0
    private var counterAnnouncementType = 1
    private var bitmapList = mutableListOf<Bitmap>()
    private val objectsLocalStorage = ObjectsLocalStorage()
    private var user: UserAPI? = null
    private var community: CommunityAPI? = null
    private var imageUpload = ImageUpload()

    private val announcementRepository = AnnoucementRepository(FeaturesApiInstance.service)
    private val publicationRepository = PublicationRepository(FeaturesApiInstance.service)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        user = objectsLocalStorage.getObjectFromLocalStorage(
            requireActivity(),
            Constants.KEY_OBJ_USER,
            UserAPI::class.java
        )
        Log.d("CreatePublicationFragment", user.toString())
        community = objectsLocalStorage.getObjectFromLocalStorage(
            requireActivity(),
            Constants.KEY_OBJ_COMMUNITY,
            CommunityAPI::class.java
        )
        Log.d("CreatePublicationFragment", community.toString())
        binding = FragmentCreatePublicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listImageRecycleView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        binding.btnGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pickImages.launch(galleryIntent)
        }

        binding.btnPublication.setOnClickListener {
            binding.btnPublication.setBackgroundResource(R.drawable.borda_cinza_fundo_azul)
            binding.btnAnnouncement.setBackgroundResource(R.drawable.fundo_azul)
            binding.btnSale.setBackgroundResource(R.drawable.fundo_azul)
            binding.btnService.setBackgroundResource(R.drawable.fundo_azul)
            binding.btnDonation.setBackgroundResource(R.drawable.fundo_azul)
            counterType = 0
            binding.typeSelection?.visibility  = View.GONE
        }

        binding.btnAnnouncement.setOnClickListener {
            binding.btnPublication.setBackgroundResource(R.drawable.fundo_azul)
            binding.btnAnnouncement.setBackgroundResource(R.drawable.borda_cinza_fundo_azul)
            binding.btnSale.setBackgroundResource(R.drawable.fundo_azul)
            binding.btnService.setBackgroundResource(R.drawable.fundo_azul)
            binding.btnDonation.setBackgroundResource(R.drawable.fundo_azul)
            counterType = 1
            binding.typeSelection?.visibility  = View.VISIBLE
            binding.btnSale?.setOnClickListener {
                binding.btnPublication.setBackgroundResource(R.drawable.fundo_azul)
                binding.btnAnnouncement.setBackgroundResource(R.drawable.borda_cinza_fundo_azul)
                binding.btnSale.setBackgroundResource(R.drawable.borda_cinza_fundo_azul)
                binding.btnService.setBackgroundResource(R.drawable.fundo_azul)
                binding.btnDonation.setBackgroundResource(R.drawable.fundo_azul)
                counterAnnouncementType = 1
            }
            binding.btnService?.setOnClickListener {
                binding.btnPublication.setBackgroundResource(R.drawable.fundo_azul)
                binding.btnAnnouncement.setBackgroundResource(R.drawable.borda_cinza_fundo_azul)
                binding.btnSale.setBackgroundResource(R.drawable.fundo_azul)
                binding.btnService.setBackgroundResource(R.drawable.borda_cinza_fundo_azul)
                binding.btnDonation.setBackgroundResource(R.drawable.fundo_azul)
                counterAnnouncementType = 2
            }
            binding.btnDonation?.setOnClickListener {
                binding.btnPublication.setBackgroundResource(R.drawable.fundo_azul)
                binding.btnAnnouncement.setBackgroundResource(R.drawable.borda_cinza_fundo_azul)
                binding.btnSale.setBackgroundResource(R.drawable.fundo_azul)
                binding.btnService.setBackgroundResource(R.drawable.fundo_azul)
                binding.btnDonation.setBackgroundResource(R.drawable.borda_cinza_fundo_azul)
                counterAnnouncementType = 3
            }
        }

        binding.btnPublish.setOnClickListener {
            binding.progressBar?.visibility = View.VISIBLE
            if (counterType == 0) {
                createPublication{
                    showNotification(
                        "Publicação criada com sucesso",
                        "Sua publicação foi criada com sucesso"
                    )
                }
            } else if (counterType == 1) {
                val announcementType = when (counterAnnouncementType) {
                    1 -> "sale"
                    2 -> "service"
                    3 -> "donation"
                    else -> "unknown"
                }
                createAnnouncement(announcementType){
                    showNotification(
                        "Anuncio criado com sucesso",
                        "Seu anuncio foi criado com sucesso"
                    )
                }
            }
        }
    }
    private fun showNotification(title: String, body: String) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            return
        }

        val intentAndroid = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intentAndroid, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(requireContext(), "channel_id")
            .setSmallIcon(R.drawable.olimpo_logo)
            .setContentTitle(title)  // Título da notificação
            .setContentText(body)  // Subtítulo da notificação
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // Prioridade da notificação
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Config Canal de Notificação
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Verifique se o canal já existe
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channelName = "Notificar"
            val channelDescription = "Canal para notificações importantes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Crie o canal apenas se ele ainda não existir
            if (notificationManager.getNotificationChannel(channelId) == null) {
                notificationManager.createNotificationChannel(channel)
            }
        }

        // Show - Apresentar Notificação
        val notificationManagerCompat = NotificationManagerCompat.from(requireContext())
        notificationManagerCompat.notify(1, builder.build())
    }

    private fun createPublication(
        onSuccess: () -> Unit
    ) {
        lifecycleScope.launch {
            if (binding.editText.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Preencha o campo de descrição", Toast.LENGTH_SHORT).show()
                return@launch
            } else if (bitmapList.isEmpty()) {
                Toast.makeText(requireContext(), "Selecione pelo menos uma imagem", Toast.LENGTH_SHORT).show()
                return@launch
            } else if (user == null || community == null) {
                Toast.makeText(requireContext(), "Erro ao criar publicação", Toast.LENGTH_SHORT).show()
                Log.e("CreatePublicationFragment", "Erro ao criar publicação: Usuário ou comunidade nulos $user ------- $community")
                return@launch
            }

            val imageList = getImageList()
            val publication = Publication(
                null,
                community?.id.toString(),
                user?.id.toString(),
                user?.name.toString(),
                user?.profileImage.toString(),
                imageList,
                binding.editText.text.toString(),
                mutableListOf()
            )
            val response = publicationRepository.createPublication(publication)
            if (response.isSuccessful) {
                onSuccess()
            } else  {
                Toast.makeText(requireContext(), "Erro ao criar publicação", Toast.LENGTH_SHORT).show()
                Log.e(
                    "CreatePublicationFragment",
                    "Erro ao criar publicação: ${response.code()} - ${response.message()}"
                )
            }

        }
    }

    private fun createAnnouncement(
        announcementType: String,
        onSuccess: () -> Unit
    ) {
        lifecycleScope.launch {
            val imageList = getImageList()
            val announcement = AnnouncementAPI(
                null,
                community?.id.toString(),
                user?.id.toString(),
                user?.name.toString(),
                user?.profileImage.toString(),
                imageList,
                binding.editText.text.toString(),
                announcementType,
                null
            )
            val response = announcementRepository.createAnnouncement(announcement)

            if (response.isSuccessful) {
                Toast.makeText(requireContext(), "Anuncio criado com sucesso", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else  {
                Toast.makeText(requireContext(), "Erro ao criar anuncio", Toast.LENGTH_SHORT).show()
                Log.e(
                    "CreateAnnouncementFragment",
                    "Erro ao criar anuncio: ${response.code()} - ${response.message()}"
                )
            }
        }
    }

    private suspend fun getImageList(): List<String> {
        return withContext(Dispatchers.IO) {
            imageUpload.uploadImageList(bitmapList)
        }
    }

    private val pickImages: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            bitmapList.clear()  // Limpa a lista atual para substituir pela nova seleção

            val clipData = result.data?.clipData
            if (clipData != null) {
                // Limita a seleção a no máximo 5 imagens
                for (i in 0 until minOf(clipData.itemCount, 5)) {
                    val imageUri = clipData.getItemAt(i).uri
                    val inputStream = imageUri.let { context?.contentResolver?.openInputStream(it) }
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        bitmapList.add(bitmap)
                    }
                }
            } else {
                // Caso uma única imagem tenha sido selecionada
                val imageUri = result.data?.data
                val inputStream = imageUri?.let { context?.contentResolver?.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    bitmapList.add(bitmap)
                }
            }

            // Atualiza a RecyclerView com a nova lista de imagens selecionadas
            imageAdapter.images = bitmapList.toList().toMutableList()
            imageAdapter.notifyDataSetChanged()
        }
    }
}
