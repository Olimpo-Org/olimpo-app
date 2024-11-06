package com.example.olimpo_app.presentation.adapters

import ImageUrlAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.databinding.ItemPublicationBinding

class PublicationAdapter : RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>() {

    inner class PublicationViewHolder(val binding: ItemPublicationBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Publication>() {
        override fun areItemsTheSame(oldItem: Publication, newItem: Publication): Boolean {
            return oldItem.publicationId == newItem.publicationId // Compare using "publicationId"
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
            ItemPublicationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        val item = postsList[position]
        holder.binding.apply {
            // Set user photo
            Glide.with(holder.itemView.context)
                .load(item.images[0])
                .into(userPhoto)

            // Set username and description
            username.text = item.senderName
            description.text = item.description

            // Set likes count
            textView3.text = item.likes?.size.toString()

            // Initialize ImageUrlAdapter for horizontal image list
            val imageUrlAdapter = ImageUrlAdapter(item.images)
            recyclerView.adapter = imageUrlAdapter
            recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }
}
