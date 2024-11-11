package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.messageFlow.ChatMessage
import com.example.olimpo_app.databinding.ItemMessageReceiveBinding
import com.example.olimpo_app.databinding.ItemMessageSendBinding

class ChatAdapter(private val chatMessages: List<ChatMessage>, private var receiverProfileImage: String, private val senderId: String ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    fun setReceiverProfileImage(bitmap: String){
        receiverProfileImage = bitmap
    }

    inner class SentMessageViewHolder(private val binding: ItemMessageSendBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(message: ChatMessage) = with(binding){
            textMessage.text = message.message
            textDateTime.text = message.dateTime
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceiveBinding): RecyclerView.ViewHolder(binding.root){
        fun setData(message: ChatMessage) = with(binding){
            textMessage.text = message.message
            textDateTime.text = message.dateTime
            Glide.with(this.imageProfile)
                .load(receiverProfileImage)
                .override(1800, 1800)
                .placeholder(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageProfile)
//            if(receiverProfileImage != null) {
//                imageProfile.setImage(receiverProfileImage)

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_SENT){
            return SentMessageViewHolder(
                ItemMessageSendBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }else{
            return ReceivedMessageViewHolder(
                ItemMessageReceiveBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = chatMessages.size

    override fun getItemViewType(position: Int): Int {
        return if(chatMessages[position].senderId.toString() == senderId){
            VIEW_TYPE_SENT
        }else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            (holder as SentMessageViewHolder).setData(chatMessages[position])
        }else{
            (holder as ReceivedMessageViewHolder).setData(chatMessages[position])
        }
    }
}