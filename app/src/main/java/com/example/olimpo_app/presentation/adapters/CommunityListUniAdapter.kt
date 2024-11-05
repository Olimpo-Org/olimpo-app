package com.example.olimpo_app.presentation.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.data.model.accessFlow.Community
import com.example.olimpo_app.databinding.ItemComunidadeBinding
import com.example.olimpo_app.presentation.listeners.CommunityListener

class CommunityListUniAdapter (
    private val communities: List<Community>,
    private val communityListener: CommunityListener
) : RecyclerView.Adapter<CommunityListUniAdapter.ConversionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        val binding = ItemComunidadeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ConversionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(communities[position])
    }

    override fun getItemCount(): Int {
        return communities.size
    }

    inner class ConversionViewHolder(private val binding: ItemComunidadeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(community: Community) {
            binding.username.text = community.name
            binding.root.setOnClickListener {
                val community = Community(
                    name = community.name.toString(),
                    image = community.image.toString(),
                    null,
                    id = community.id.toString()
                )
                communityListener.onCommunityClicked(community)
            }
            Glide.with(binding.root.context)
                .load(community.image)
                .into(binding.communityPhoto)
        }
    }
}