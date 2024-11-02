package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import com.example.olimpo_app.databinding.ItemShopBinding

class AnnoucementAdapter : RecyclerView.Adapter<AnnoucementAdapter.AnnoucementViewHolder>() {

    inner class AnnoucementViewHolder(val binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {

            return if (oldItem is AnnouncementAPI && newItem is AnnouncementAPI) {
                oldItem.announcementId == newItem.announcementId
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is AnnouncementAPI && newItem is AnnouncementAPI) {
                oldItem == newItem
            } else {
                false
            }
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var postsList: List<Any>
        get() = differ.currentList
        set(value) { differ.submitList(value) }

    override fun getItemCount() = postsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnoucementViewHolder {
        return AnnoucementViewHolder(
            ItemShopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnnoucementViewHolder, position: Int) {
        holder.binding.apply {
            val item = postsList[position]

            if (item is AnnouncementAPI) {
                OlimpoFoto.alpha = 1.0f // Ajuste do alpha da imagem
                username.text = item.senderName
                // Usando o adapter de imagens com MutableList
                recyclerView.adapter = SelectedImagesAdapter(item.images.toMutableList())
                description.text = item.description
            }
        }
    }
}