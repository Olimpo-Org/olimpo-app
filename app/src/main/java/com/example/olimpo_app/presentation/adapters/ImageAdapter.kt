package com.example.olimpo_app.presentation.fragment.feedFlow

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.databinding.ItemFotosBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val images = mutableListOf<Bitmap>()

    fun addImage(image: Bitmap) {
        images.add(image)
        notifyItemInserted(images.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemFotosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(private val binding: ItemFotosBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Bitmap) {
            binding.image.setImageBitmap(image)
        }
    }
}