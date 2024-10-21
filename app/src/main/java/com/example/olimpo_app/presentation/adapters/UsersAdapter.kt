package com.example.olimpo_app.presentation.adapters


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.databinding.ItemUsersBinding
import com.example.olimpo_app.presentation.listeners.UserListener


class UsersAdapter(private val users: List<User>, val listener: UserListener): RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {
    inner class UserViewHolder(private val binding: ItemUsersBinding): RecyclerView.ViewHolder(binding.root) {
        fun setUserData(user: User) = with(binding){
            username.text = user.name
            OlimpoFoto.setImageBitmap(getUserImage(user.image.toString()))
            root.setOnClickListener{listener.onUserClicked(user)}
        }
        private fun getUserImage(encodedString: String): Bitmap {
            val bytes = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemContainerUserBinding = ItemUsersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(itemContainerUserBinding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }
}