package com.example.olimpo_app.presentation.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.R
import java.io.ByteArrayInputStream

class SelectedImagesAdapter(private var imagesList: MutableList<String>) : RecyclerView.Adapter<SelectedImagesAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView) // Certifique-se de que o ID esteja correto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fotos, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageBase64 = imagesList[position]
        val bitmap = decodeBase64ToBitmap(imageBase64)
        holder.imageView.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int = imagesList.size

    fun updateImages(newImages: List<String>) {
        imagesList.clear()
        imagesList.addAll(newImages)
        notifyDataSetChanged()
    }

    private fun decodeBase64ToBitmap(base64: String): Bitmap? {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes))
    }
}