package com.example.olimpo_app.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.databinding.ItemSolicitCommunityBinding
import com.example.olimpo_app.presentation.listeners.SolicitListener

class SolicitCommunityAdapter(
    private val communities: List<CommunityAPI>,
    private val listener: SolicitListener
) : RecyclerView.Adapter<SolicitCommunityAdapter.CommunityViewHolder>() {

    inner class CommunityViewHolder(private val binding: ItemSolicitCommunityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(community: CommunityAPI) {
            binding.username.text = community.name

            Glide.with(binding.root.context)
                .load(community.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.OlimpoFoto)

            binding.btnAccept.setOnClickListener {
                listener.onSolicitClicked(community.id ?: -1)
                binding.btnAccept.setTextColor(binding.root.context.getColor(R.color.Grey_Standard))
                binding.btnAccept.setBackgroundResource(R.drawable.borda_cinza)
                binding.btnAccept.isEnabled = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding = ItemSolicitCommunityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CommunityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind(communities[position])
    }

    override fun getItemCount(): Int = communities.size
}
