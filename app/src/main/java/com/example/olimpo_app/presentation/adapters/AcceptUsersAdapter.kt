package com.example.olimpo_app.presentation.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.data.models.User
import com.example.olimpo_app.databinding.ItemUsuariosBinding
import com.example.olimpo_app.listeners.UserListener

class AcceptUsersAdapter(private val users: List<User>, val listener: UserListener): RecyclerView.Adapter<AcceptUsersAdapter.AcceptUserViewHolder>() {
    inner class AcceptUserViewHolder(private val binding: ItemUsuariosBinding): RecyclerView.ViewHolder(binding.root) {
        fun setAcceptUserData(user: User) = with(binding){
            username.text = user.name
            OlimpoFoto.setImageBitmap(getAcceptUserImage(user.image.toString()))
            root.setOnClickListener{listener.onUserClicked(user)}
        }
        private fun getAcceptUserImage(encodedString: String): Bitmap {
            val bytes = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptUserViewHolder {
        val itemContainerUserBinding = ItemUsuariosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AcceptUserViewHolder(itemContainerUserBinding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: AcceptUserViewHolder, position: Int) {
        holder.setAcceptUserData(users[position])
    }
}