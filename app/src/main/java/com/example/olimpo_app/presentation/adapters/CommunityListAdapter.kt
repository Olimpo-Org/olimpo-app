package com.example.olimpo_app.presentation.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.data.models.Community
import com.example.olimpo_app.databinding.ItemComunidadesBinding
import com.example.olimpo_app.listeners.CommunityListener

class CommunityListAdapter(
    private val communities: List<Community>,
    private val communityListener: CommunityListener
) : RecyclerView.Adapter<CommunityListAdapter.ConversionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        val binding = ItemComunidadesBinding.inflate(
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

    inner class ConversionViewHolder(private val binding: ItemComunidadesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(community: Community) {
            binding.OlimpoFoto.setImageBitmap(getConversionImage(community.image))
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
        }
    }

    // Método para converter a string Base64 em Bitmap
    private fun getConversionImage(image: String?): Bitmap? {

        if (image.isNullOrEmpty()) {
            Log.e("CommunityListAdapter", "Imagem está nula ou vazia.")
            return null
        }

        val bytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}