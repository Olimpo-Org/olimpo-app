package com.example.olimpo_app.presentation.fragment.messageFlow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.olimpo_app.AccessApiInstance
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.repository.CommunityRepository
import com.example.olimpo_app.databinding.FragmentFindChatBinding
import com.example.olimpo_app.presentation.activity.messageFlow.ChatActivity
import com.example.olimpo_app.presentation.adapters.UserAdapter
import com.example.olimpo_app.presentation.listeners.UserListener
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FindChatFragment : Fragment(), UserListener {

    private lateinit var binding: FragmentFindChatBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var userAdapter: UserAdapter
    private var apiUserList: MutableList<UserAPI> = mutableListOf()
    private val accessAPI = AccessApiInstance.service
    private val communityRepository = CommunityRepository(accessAPI)
    private var objectsLocalStorage = ObjectsLocalStorage()
    private val communityId by lazy { preferenceManager.getString(Constants.KEY_COMMUNITY_API_ID)?.toIntOrNull() ?: 0 }

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
        setAdapter()
        getUsersFromCommunity()
    }
    private fun getUsersFromCommunity() {
        loading(true)
        val database = FirebaseFirestore.getInstance()

        // Recupera o communityId do PreferenceManager
        val communityId = preferenceManager.getString(Constants.KEY_COMMUNITY_ID)

        if (communityId == null) {
            loading(false)
            return
        }

        // Buscar a comunidade para obter a lista de membros
        database.collection(Constants.KEY_COLLECTION_COMMUNITY)
            .document(communityId)
            .get()
            .addOnSuccessListener { communityDocument ->
                loading(false)

                if (communityDocument.exists()) {
                    val memberIds = communityDocument.get(Constants.KEY_COMMUNITY_MEMBERS) as? List<String>

                    if (!memberIds.isNullOrEmpty()) {
                        // Agora busca os detalhes dos membros a partir da coleção de usuários
                        getUserList()
                    } else {

                    }
                } else {

                }
            }
            .addOnFailureListener { e ->
                loading(false)

            }
    }

    private fun getUserList() {
        loading(true)
        lifecycleScope.launch {
            try {
                apiUserList = communityRepository.getAllUsersInCommunity(
                    communityId = communityId
                ).body()?.toMutableList() ?: mutableListOf()

                if (apiUserList.isNotEmpty()) {
                    setAdapter()
                    binding.userList.visibility = View.VISIBLE
                    loading(false)
                } else {
                    loading(false)
                    binding.userList.visibility = View.GONE
                }
            } catch (e: Exception) {
                loading(false)
                binding.userList.visibility = View.GONE
            }
        }
    }
    override fun onUserClicked(user: User, userAPI: UserAPI) {
        objectsLocalStorage.cleanObjectFromLocalStorage(requireContext(), Constants.KEY_OBJ_USER)
        objectsLocalStorage.saveObjectInLocalStorage(requireContext(), Constants.KEY_OBJ_USER, user)
        val intent = Intent(context, ChatActivity::class.java)
        startActivity(intent)
    }
        private fun setAdapter() {
            userAdapter = UserAdapter(apiUserList, this@FindChatFragment)
            binding.userList.adapter = userAdapter
    }
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}