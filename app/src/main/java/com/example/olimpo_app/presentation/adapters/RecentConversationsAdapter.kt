package com.example.olimpo_app.presentation.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.data.models.ChatMessage
import com.example.olimpo_app.data.models.User
import com.example.olimpo_app.databinding.ItemChatsBinding
import com.example.olimpo_app.listeners.ConversionListener

class RecentConversationsAdapter(
    private val chatMessages: List<ChatMessage>,
    private val conversionListener: ConversionListener
) : RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>() {

//    class RecentConversationsAdapter(
//        private val chatMessages: List<ChatMessage>,
//        private val conversionListener: ConversionListener
//    )


    inner class ConversionViewHolder(private val binding: ItemChatsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(chatMessage: ChatMessage) {
            binding.OlimpoFoto.setImageBitmap(getConversionImage(chatMessage.conversionImage))
            binding.textView.text = chatMessage.conversionName
            binding.textView2.text = chatMessage.message
            binding.root.setOnClickListener {
                val user = User(
                    name = chatMessage.conversionName.toString(),
                    image = chatMessage.conversionImage.toString(),
                    null,
                    null,
                    id = chatMessage.conversionId.toString()
                )
                conversionListener.onConversionClicked(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        val binding = ItemChatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessages[position])
    }

    private fun getConversionImage(encodedImage: String?): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}