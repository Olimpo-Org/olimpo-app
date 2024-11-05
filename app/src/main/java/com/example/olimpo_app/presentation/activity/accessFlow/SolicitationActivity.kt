package com.example.olimpo_app.presentation.activity.accessFlow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.data.model.accessFlow.Solicitation
import com.example.olimpo_app.data.repository.CommunityRepository
import com.example.olimpo_app.databinding.ActivitySolicitacaoBinding
import com.example.olimpo_app.presentation.activity.BaseActivity
import com.example.olimpo_app.presentation.adapters.SolicitationAdapter
import com.example.olimpo_app.presentation.listeners.AcceptSolicitationListener
import com.example.olimpo_app.presentation.listeners.RejectSolicitationListener
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import kotlinx.coroutines.launch
import java.util.UUID

class SolicitationActivity : BaseActivity(), AcceptSolicitationListener, RejectSolicitationListener {
    private lateinit var binding: ActivitySolicitacaoBinding
    private lateinit var preferenceManager: PreferenceManager
    private val communityRepository = CommunityRepository(AccessApiInstance.service)
    private val userId by lazy { preferenceManager.getString(Constants.KEY_API_USER_ID)?.toIntOrNull() ?: 0 }
    private var solicitationList: MutableList<Solicitation> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolicitacaoBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(applicationContext)
        setContentView(binding.root)

        setListeners()
        getSolicitationsApi()
    }

    private fun getSolicitationsApi() {
        lifecycleScope.launch {
            try {
                val response = communityRepository.getAllSolicitationsByUser(userId)
                if (response.isSuccessful && response.body() != null) {
                    solicitationList = response.body()!!.toMutableList()
                    val solicitationAdapter = SolicitationAdapter(
                        solicitationList,
                        this@SolicitationActivity,
                        this@SolicitationActivity
                    )
                    binding.UsersRecyclerView.adapter = solicitationAdapter
                    binding.UsersRecyclerView.visibility = View.VISIBLE
                } else {
                    showToast("Failed to fetch users.")
                }
            } catch (e: Exception) {
                showToast("An error occurred: ${e.message}")
            }
        }
    }
    private fun setListeners() {
        binding.buttonSignOut.setOnClickListener { signOut() }
        binding.buttonArrow.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun signOut() {
        preferenceManager.clear()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    override fun onAcceptSolicitationClicked(solicitationId: UUID?) {
        lifecycleScope.launch {
            try {
                for (solicitation in solicitationList) {
                    if (solicitation.id == solicitationId) {
                        solicitationList.remove(solicitation)
                        val solicitationAdapter = SolicitationAdapter(
                            solicitationList,
                            this@SolicitationActivity,
                            this@SolicitationActivity
                        )
                        binding.UsersRecyclerView.adapter = solicitationAdapter
                        binding.UsersRecyclerView.visibility = View.VISIBLE
                    }
                }
                val response = solicitationId?.let { communityRepository.acceptSolicitation(it) }
                if (response?.isSuccessful == true) {
                    showToast("Solicitation accepted.")
                    getSolicitationsApi()
                } else {
                    showToast("Failed to accept solicitation.")
                }
            } catch (e: Exception) {
                showToast("An error occurred: ${e.message}")
            }
        }
    }

    override fun onRejectSolicitationClicked(solicitationId: UUID?) {
        lifecycleScope.launch {
            try {
                for (solicitation in solicitationList) {
                    if (solicitation.id == solicitationId) {
                        solicitationList.remove(solicitation)
                        val solicitationAdapter = SolicitationAdapter(
                            solicitationList,
                            this@SolicitationActivity,
                            this@SolicitationActivity
                        )
                        binding.UsersRecyclerView.adapter = solicitationAdapter
                        binding.UsersRecyclerView.visibility = View.VISIBLE
                    }
                }
                val response = solicitationId?.let { communityRepository.rejectSolicitation(it) }
                if (response?.isSuccessful == true) {
                    showToast("Solicitation rejected.")
                    getSolicitationsApi()
                } else {
                    showToast("Failed to reject solicitation.")
                }
            } catch (e: Exception) {
                showToast("An error occurred: ${e.message}")
            }
        }
    }
}
