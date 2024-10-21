package com.example.olimpo_app.presentation.fragment.messageFlow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.databinding.FragmentFindChatBinding
import com.example.olimpo_app.presentation.listeners.UserListener
import com.example.olimpo_app.presentation.listeners.UsersCallback
import com.example.olimpo_app.presentation.activity.messageFlow.ChatActivity
import com.example.olimpo_app.presentation.adapters.UsersAdapter
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
class FindChatFragment : Fragment(), UserListener {

    private lateinit var binding: FragmentFindChatBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindChatBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())
        getUsersFromCommunity(object : UsersCallback {
            override fun onUsersLoaded(users: List<User>) {
                // Configurar o adapter e mostrar a lista de usuários
                val usersAdapter = UsersAdapter(users, this@FindChatFragment)
                binding.userList.adapter = usersAdapter
                binding.userList.visibility = View.VISIBLE
            }

            override fun onError(message: String) {
                // Exibir mensagem de erro ou lidar com o erro
                binding.userList.visibility = View.GONE
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getUsersFromCommunity(callback: UsersCallback) {
        loading(true)
        val database = FirebaseFirestore.getInstance()

        // Recupera o communityId do PreferenceManager
        val communityId = preferenceManager.getString(Constants.KEY_COMMUNITY_ID)

        if (communityId == null) {
            loading(false)
            callback.onError("Community ID is null")
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
                        fetchUsersDetails(memberIds, callback)
                    } else {
                        callback.onError("No members found in community")
                    }
                } else {
                    callback.onError("Community not found")
                }
            }
            .addOnFailureListener { e ->
                loading(false)
                callback.onError("Error fetching community: ${e.message}")
            }
    }

    private fun fetchUsersDetails(memberIds: List<String>, callback: UsersCallback) {
        val database = FirebaseFirestore.getInstance()
        val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
        val users = mutableListOf<User>()

        binding.userList.layoutManager = LinearLayoutManager(requireContext())

        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereIn(FieldPath.documentId(), memberIds)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    for (queryDocumentSnapshot in it.result) {
                        if (currentUserId == queryDocumentSnapshot.id) continue

                        val user = User(
                            name = queryDocumentSnapshot.getString(Constants.KEY_NAME)!!,
                            image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)!!,
                            email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)!!,
                            token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN),
                            id = queryDocumentSnapshot.id
                        )
                        users.add(user)
                    }

                    if (users.isNotEmpty()) {
                        callback.onUsersLoaded(users)
                    } else {
                        callback.onError("No users found")
                    }
                } else {
                    callback.onError("Error fetching users")
                }
            }
            .addOnFailureListener { e ->
                callback.onError("Error fetching users: ${e.message}")
            }
    }


    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onUserClicked(user: User) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        preferenceManager.putString(Constants.KEY_RECEIVER_ID, user.id)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }
}