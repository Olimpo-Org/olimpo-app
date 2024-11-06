package com.example.olimpo_app.presentation.adapters

import ImageUrlAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import com.example.olimpo_app.databinding.ItemShopBinding
import com.example.olimpo_app.presentation.listeners.GoToConversationClicked

class AnnouncementAdapter(
    private val context: Context,
    private val goToConversationClicked: GoToConversationClicked
) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    inner class AnnouncementViewHolder(val binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<AnnouncementAPI>() {
        override fun areItemsTheSame(oldItem: AnnouncementAPI, newItem: AnnouncementAPI): Boolean {
            return oldItem.announcementId == newItem.announcementId
        }

        override fun areContentsTheSame(oldItem: AnnouncementAPI, newItem: AnnouncementAPI): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var postsList: List<AnnouncementAPI>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun getItemCount() = postsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val binding = ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnouncementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val item = postsList[position]
        holder.binding.apply {
            description.text = item.description
            username.text = item.senderName

            // Configura o ImageUrlAdapter para o RecyclerView de imagens
            val imageUrlAdapter = ImageUrlAdapter(item.images)
            recyclerView.adapter = imageUrlAdapter
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            // Carrega a primeira imagem usando Glide
            if (item.images.isNotEmpty()) {
                Glide.with(root)
                    .load(item.senderImage)
                    .into(userPhoto)
            }

            // Define o clique para ir à conversa
            root.setOnClickListener {
                goToConversationClicked.onGoToConversationClicked(item.senderId.toInt())
            }
        }
    }
}
