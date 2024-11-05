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
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olimpo_app.R
import com.example.olimpo_app.databinding.FragmentCreatePublicationBinding
import com.example.olimpo_app.utils.NotificationReceiver
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class CreatePublicationFragment : Fragment() {
    private lateinit var binding: FragmentCreatePublicationBinding
    private var encodedImage: String? = null
    private val imageAdapter = ImageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePublicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuração do RecyclerView para mostrar as imagens
        binding.listImageRecycleView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        // Exibe o footer ao clicar no botão de anúncio
        binding.btnAnnouncement.setOnClickListener {
            binding.footer.visibility = View.VISIBLE
            binding.btnAnnouncement.setBackgroundResource(R.drawable.fundo_azul)
            binding.btnPublication.setBackgroundResource(R.drawable.fundo_branco)
        }
        binding.btnPublication.setOnClickListener {
            binding.footer.visibility = View.GONE
            binding.btnPublication.setBackgroundResource(R.drawable.fundo_azul)
            binding.btnAnnouncement.setBackgroundResource(R.drawable.fundo_branco)
        }

        binding.btnPublish.setOnClickListener {
            notificar()
        }

        // Ação para selecionar uma imagem da galeria
        binding.btnGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(galleryIntent)
        }
    }
    // Método para mandar notificação ao publicar
    fun notificar() {
        // Verifique se a permissão para postar notificações foi concedida
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Solicite a permissão
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            return
        }

        // Criar a notificação
        val intentAndroid = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intentAndroid, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(requireContext(), "channel_id")
            .setSmallIcon(R.drawable.olimpo_logo)
            .setContentTitle("Parabéns, publicação criada com sucesso🎉")  // Título da notificação
            .setContentText("publicação quentinha saindo🥳")  // Subtítulo da notificação
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


    // Método para codificar a imagem em base64
    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    // Launcher para abrir a galeria e selecionar uma imagem
    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageUri = result.data?.data
            try {
                val inputStream = imageUri?.let { uri -> context?.contentResolver?.openInputStream(uri) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                encodedImage = encodeImage(bitmap)
                // Adiciona a imagem ao adaptador e atualiza o RecyclerView
                imageAdapter.addImage(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}