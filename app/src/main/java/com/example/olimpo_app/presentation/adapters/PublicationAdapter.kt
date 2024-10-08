package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.olimpo_app.R
import com.example.olimpo_app.data.models.Publication

class PublicationAdapter: RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>() {
    private var publications: MutableList<Publication> = mutableListOf()
    fun setPublication(publications: MutableList<Publication>) {
        this.publications = publications
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return PublicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        val publication = publications[position]
        holder.bind(publication)
    }

    override fun getItemCount(): Int {
        return publications.size
    }

    class PublicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageProfile: ImageView = itemView.findViewById(R.id.OlimpoFoto)
        private val username: TextView = itemView.findViewById(R.id.username)
        private val posterImage: ImageView  = itemView.findViewById(R.id.recyclerView)
        private val description: TextView = itemView.findViewById(R.id.description)

        fun bind(publication: Publication) {
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w500/${publication.sender_image}")
                .placeholder(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageProfile)
            username.text = publication.sender_name
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w500/${publication.images}")
                .placeholder(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(posterImage)
            description.text = publication.description
        }
    }
}