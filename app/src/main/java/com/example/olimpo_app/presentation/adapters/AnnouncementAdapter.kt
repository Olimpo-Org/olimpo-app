package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import com.example.olimpo_app.databinding.ItemShopBinding
import com.example.olimpo_app.presentation.listeners.GoToConversationClicked

class AnnouncementAdapter(
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
            recyclerView.adapter = ImageUrlAdapter(item.images)
            Glide.with(root)
                .load(item.images[0])
                .into(userPhoto)

            root.setOnClickListener {
                goToConversationClicked.onGoToConversationClicked(
                    item.senderId.toInt()
                )
            }
        }
    }
}
