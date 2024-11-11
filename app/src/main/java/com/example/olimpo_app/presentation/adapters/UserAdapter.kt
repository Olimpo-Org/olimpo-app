package com.example.olimpo_app.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.presentation.listeners.UserListener

class UserAdapter(
    private val users: MutableList<UserAPI>,
    private val userClickListener: UserListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_users, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username: TextView = itemView.findViewById(R.id.username)
        private val userImage: ImageView = itemView.findViewById(R.id.OlimpoFoto)

        fun bind(user: UserAPI) {
            username.text = user.name ?: "Usuário desconhecido"

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.placeholder_image) // Placeholder enquanto carrega

            Glide.with(itemView.context)
                .load(user.profileImage.takeIf { !it.isNullOrEmpty() }) // Carrega se não for nulo ou vazio
                .apply(requestOptions)
                .into(userImage)

            itemView.setOnClickListener {
                userClickListener.onUserClicked(
                    User(
                        name = user.name,
                        image = user.profileImage,
                        email = null,
                        token = null,
                        id = user.id,
                        apiId = user.id
                    ),
                    user
                )
            }
        }
    }
}