package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.Community
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.databinding.ItemComunidadeBinding
import com.example.olimpo_app.presentation.listeners.CommunityClickListener

class CommunityAdapter(
    private val communityList: List<CommunityAPI>,
    private val communityClickListener: CommunityClickListener
) : RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding = ItemComunidadeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind(communityList[position])
    }

    override fun getItemCount() = communityList.size

    inner class CommunityViewHolder(private val binding: ItemComunidadeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(community: CommunityAPI) {
            binding.username.text = community.name
            Glide.with(binding.root.context)
                .load(community.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.communityPhoto)

            binding.root.setOnClickListener {
                communityClickListener.onCommunityClicked(
                    Community(
                        name = community.name,
                        image = community.imageUrl,
                        token = null,
                        id = null,
                        userId = null,
                        communityApiId = community.id.toString()
                    ),
                    community
                )
            }
        }
    }
}
