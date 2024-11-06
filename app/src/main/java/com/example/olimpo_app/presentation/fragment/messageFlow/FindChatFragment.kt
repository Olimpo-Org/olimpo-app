package com.example.olimpo_app.presentation.fragment.messageFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.repository.CommunityRepository
import com.example.olimpo_app.databinding.FragmentFindChatBinding
import com.example.olimpo_app.presentation.activity.messageFlow.ChatActivity
import com.example.olimpo_app.presentation.adapters.UserAdapter
import com.example.olimpo_app.presentation.listeners.UserListener
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import kotlinx.coroutines.launch

class FindChatFragment : Fragment(), UserListener {

    private lateinit var binding: FragmentFindChatBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var userAdapter: UserAdapter
    private val apiUserList = mutableListOf<UserAPI>()
    private val accessAPI = AccessApiInstance.service
    private val communityRepository = CommunityRepository(accessAPI)

    private val communityId: Int
        get() = 1
            // preferenceManager.getString(Constants.KEY_COMMUNITY_API_ID)?.toIntOrNull() ?: 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindChatBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchUsersFromCommunity()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(apiUserList, this)
        binding.userList.layoutManager = LinearLayoutManager(context)
        binding.userList.adapter = userAdapter
    }

    private fun fetchUsersFromCommunity() {
        loading(true)
        Log.d("FindChatFragment", "Community ID: $communityId") // Verificar o ID da comunidade
        lifecycleScope.launch {
            try {
                val response = communityRepository.getAllUsersInCommunity(communityId)
                if (response.isSuccessful) {
                    val users = response.body()
                    Log.d("FindChatFragment", "Resposta da API: $users") // Log da resposta

                    if (users != null && users.isNotEmpty()) {
                        apiUserList.clear()
                        apiUserList.addAll(users)
                        userAdapter.notifyDataSetChanged()
                        binding.userList.visibility = View.VISIBLE
                    } else {
                        showErrorToast("Nenhum usuário encontrado.")
                    }
                } else {
                    showErrorToast("Erro na resposta da API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FindChatFragment", "Erro ao buscar usuários: ${e.message}")
                showErrorToast("Erro ao buscar usuários.")
            } finally {
                loading(false)
            }
        }
    }

    override fun onUserClicked(user: User, userAPI: UserAPI) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        preferenceManager.putString(Constants.KEY_USER, user.id.toString())
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        binding.userList.visibility = View.GONE
    }
}