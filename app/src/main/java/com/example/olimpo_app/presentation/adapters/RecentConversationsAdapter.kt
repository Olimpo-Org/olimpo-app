package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.model.messageFlow.ChatMessage
import com.example.olimpo_app.presentation.listeners.ConversionListener
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import com.google.firebase.storage.FirebaseStorage

private val objectsLocalStorage = ObjectsLocalStorage()

class RecentConversationsAdapter(
    private val chatMessages: List<ChatMessage>,
    private val conversionListener: ConversionListener
) : RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chats, parent, false)
        return ConversionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.bind(chatMessages[position])
    }

    override fun getItemCount(): Int = chatMessages.size

    inner class ConversionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username: TextView = itemView.findViewById(R.id.textView)
        private val lastMessage: TextView = itemView.findViewById(R.id.textView2)
        private val userImage: ImageView = itemView.findViewById(R.id.OlimpoFoto)

        fun bind(chatMessage: ChatMessage) {
            // Set username and last message
            username.text = chatMessage.conversionName ?: "Usuário"
            lastMessage.text = chatMessage.message ?: ""

            // Set Glide request options with placeholder and error images
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.placeholder_image)

            // Load image from Firebase Storage
            val imageRef = FirebaseStorage.getInstance().reference.child(chatMessage.conversionImage ?: "")
            Glide.with(itemView.context)
                .load(chatMessage.conversionImage.takeIf { !it.isNullOrEmpty() }) // Carrega se não for nulo ou vazio
                .apply(requestOptions)
                .into(userImage)

            // Set click listener to trigger conversionListener
            itemView.setOnClickListener {
                val user = User(
                    name = chatMessage.conversionName.toString(),
                    image = chatMessage.conversionImage.toString(),
                    email = null,
                    token = null,
                    id = chatMessage.conversionId?.toInt(),
                    apiId = objectsLocalStorage.getObjectFromLocalStorage(
                        it.context,
                        Constants.KEY_OBJ_USER,
                        UserAPI::class.java
                    )?.id
                )
                conversionListener.onConversionClicked(user)
            }
        }
    }
}