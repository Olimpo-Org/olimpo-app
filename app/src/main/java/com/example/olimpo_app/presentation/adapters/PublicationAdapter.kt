package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.databinding.ItemPublicationBinding

class PublicationAdapter : RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>() {

    inner class PublicationViewHolder(val binding: ItemPublicationBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Publication>() {
        override fun areItemsTheSame(oldItem: Publication, newItem: Publication): Boolean {
            return oldItem.publicationId == newItem.publicationId // Use "publicationId" to compare items
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
        holder.binding.apply {
            val item = postsList[position]
//            Glide.with(holder.itemView.context)
//                .load(item.userPhoto)
//                .into(userPhoto)
            username.text = item.senderName

            // Using the adapter for images with MutableList
            recyclerView.adapter = SelectedImagesAdapter(item.images.toMutableList())

            textView3.text = item.likes?.size.toString()
            description.text = item.description
        }
    }
}
