package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.data.model.feedFlow.AdvertisementAPI
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.databinding.ItemAdvertisementBinding
import com.example.olimpo_app.databinding.ItemPublicationBinding
import com.example.olimpo_app.presentation.listeners.OnLikeClicked
import com.example.olimpo_app.presentation.listeners.OnUserNameClicked

class FeedAdapter(
    val itemList: List<FeedItem>,
    private val onLikeClicked: OnLikeClicked,
    private val onUserNameClicked: OnUserNameClicked,
    private val userId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_PUBLICATION = 1
    private val VIEW_TYPE_ADVERTISEMENT = 2

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is FeedItem.PublicationItem -> VIEW_TYPE_PUBLICATION
            is FeedItem.AdvertisementItem -> VIEW_TYPE_ADVERTISEMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PUBLICATION -> {
                val binding = ItemPublicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PublicationViewHolder(binding)
            }
            VIEW_TYPE_ADVERTISEMENT -> {
                val binding = ItemAdvertisementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdvertisementViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Tipo de visualização desconhecido.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PublicationViewHolder -> holder.bind((itemList[position] as FeedItem.PublicationItem).publication)
            is AdvertisementViewHolder -> holder.bind((itemList[position] as FeedItem.AdvertisementItem).advertisement)
        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class PublicationViewHolder(private val binding: ItemPublicationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(publication: Publication) {
            binding.apply {
                username.text = publication.senderName
                description.text = publication.description
                textView3.text = publication.likes?.size.toString()
                recyclerView.adapter = SelectedImagesAdapter(publication.images.toMutableList())
            }
            if (publication.likes?.contains(userId.toString()) == true) {
                binding.likeBtn.setImageResource(com.example.olimpo_app.R.drawable.heartp)
            } else {
                binding.likeBtn.setImageResource(com.example.olimpo_app.R.drawable.heartl)
            }
            binding.likeBtn.setOnClickListener {
                publication.publicationId?.let { it1 -> onLikeClicked.onLikeClicked(it1, userId) }
            }

            binding.username.setOnClickListener {
                onUserNameClicked.onUserNameClicked(
                    publication.senderId.toInt(),
                    publication.senderName,
                )
            }
        }
    }

    inner class AdvertisementViewHolder(private val binding: ItemAdvertisementBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(advertisement: AdvertisementAPI) {
            binding.apply {
                advertisementTitle.text = advertisement.title
                advertisementDescription.text = advertisement.description
                Glide.with(advertisementImage.context)
                    .load(advertisement.imageUrl)
                    .into(advertisementImage)
            }
        }
    }
}


