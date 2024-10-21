package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.data.models.Publication
import com.example.olimpo_app.databinding.ItemHomeBinding

class PublicationAdapter: RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>() {
    inner class PublicationViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Publication>() {
        override fun areItemsTheSame(oldItem: Publication, newItem: Publication): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Publication, newItem: Publication): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var postsList: List<Publication>
        get() = differ.currentList
        set(value) { differ.submitList(value) }

    override fun getItemCount() = postsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        return PublicationViewHolder(
            ItemHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }
    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        holder.binding.apply {
            val posts = postsList[position]
            OlimpoFoto.imageAlpha = posts.sender_image
            username.text = posts.sender_name
            recyclerView.adapter = posts.images
            textView3.text = posts.likes
            description.text = posts.description
        }
    }
}