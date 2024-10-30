package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.databinding.ItemHomeBinding

class PublicationAdapter : RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>() {

    inner class PublicationViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            // Verificação segura para tipo Publication e igualdade de IDs
            return if (oldItem is Publication && newItem is Publication) {
                oldItem.publicationId == newItem.publicationId // Usar "publicationId" no lugar de "id"
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is Publication && newItem is Publication) {
                oldItem == newItem
            } else {
                false
            }
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var postsList: List<Any>
        get() = differ.currentList
        set(value) { differ.submitList(value) }

    override fun getItemCount() = postsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        return PublicationViewHolder(
            ItemHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        holder.binding.apply {
            val item = postsList[position]

            // Verificação se o item é uma instância de Publication antes de fazer o cast
            if (item is Publication) {
                // Configurando as propriedades da Publication
                OlimpoFoto.alpha = 1.0f // Ajuste do alpha da imagem
                username.text = item.senderName

                // Usando o adapter de imagens com MutableList
                recyclerView.adapter = SelectedImagesAdapter(item.images.toMutableList())

                textView3.text = item.likes.size.toString()
                description.text = item.description
            }
        }
    }
}