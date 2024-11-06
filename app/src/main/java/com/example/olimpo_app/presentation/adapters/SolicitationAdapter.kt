package com.example.olimpo_app.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.Solicitation
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.databinding.ItemSolicitationBinding
import com.example.olimpo_app.databinding.ItemUsuariosBinding
import com.example.olimpo_app.presentation.listeners.AcceptSolicitationListener
import com.example.olimpo_app.presentation.listeners.RejectSolicitationListener
import java.util.UUID

class SolicitationAdapter(
    private val solicitations: List<Solicitation>,
    private val acceptListener: AcceptSolicitationListener,
    private val rejectListener: RejectSolicitationListener
) : RecyclerView.Adapter<SolicitationAdapter.AcceptUserViewHolder>() {

    inner class AcceptUserViewHolder(private val binding: ItemSolicitationBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(solicitation: Solicitation) {
            binding.username.text = solicitation.userName

            binding.btnReject.background = binding.root.context.getDrawable(R.drawable.borda_azul)
            Glide.with(binding.root.context)
                .load(solicitation.userUrlImage)
                .into(binding.OlimpoFoto)

            binding.btnAccept.setOnClickListener{
                acceptListener.onAcceptSolicitationClicked(solicitation.id)
            }
            binding.btnReject.setOnClickListener {
                rejectListener.onRejectSolicitationClicked(solicitation.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptUserViewHolder {
        val binding = ItemSolicitationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AcceptUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AcceptUserViewHolder, position: Int) {
        holder.bind(solicitations[position])
    }

    override fun getItemCount(): Int = solicitations.size
}
