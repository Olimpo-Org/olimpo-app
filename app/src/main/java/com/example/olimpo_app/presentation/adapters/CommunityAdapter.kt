package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.Community
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.presentation.listeners.CommunityClickListener

class CommunityAdapter(
    private val communityList: List<CommunityAPI>,
    private val communityClickListener: CommunityClickListener
) : RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comunidade, parent, false)
        return CommunityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind(communityList[position])
    }

    override fun getItemCount() = communityList.size

    inner class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val communityName: TextView = itemView.findViewById(R.id.username)
        private val communityImage: ImageView = itemView.findViewById(R.id.OlimpoFoto)

        fun bind(community: CommunityAPI) {
            communityName.text = community.name
            Glide.with(itemView.context)
                .load(community.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(communityImage)

            itemView.setOnClickListener {
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
