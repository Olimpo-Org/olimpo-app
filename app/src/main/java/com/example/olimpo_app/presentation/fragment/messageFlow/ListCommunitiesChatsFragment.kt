package com.example.olimpo_app.presentation.fragment.messageFlow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.messageFlow.ChatMessage
import com.example.olimpo_app.databinding.FragmentListCommunitiesChatsBinding
import com.example.olimpo_app.presentation.activity.messageFlow.ChatActivity
import com.example.olimpo_app.presentation.adapters.RecentConversationsAdapter
import com.example.olimpo_app.presentation.listeners.ConversionListener
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ListCommunitiesChatsFragment : Fragment(), ConversionListener {
    private lateinit var binding: FragmentListCommunitiesChatsBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var conversations: MutableList<ChatMessage>
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListCommunitiesChatsBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())
        init()
        listenConversations()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEncontrarPessoas.setOnClickListener {
            val fragment = FindChatFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit()
        }
//        binding.btnCreateGroup.setOnClickListener {
//            val fragment = CreateForunsFragment()
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment, fragment)
//                .addToBackStack(null)
//                .commit()
//        }
    }

    private fun init() {
        conversations = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversations, this) // Passa `this` como `ConversionListener`
        binding.recentConversationsRecyclerView.apply {
            adapter = conversationsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        database = FirebaseFirestore.getInstance()
    }

    private fun listenConversations() {
        val userId = preferenceManager.getString(Constants.KEY_OBJ_USER)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, userId)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, userId)
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        binding.layoutError.visibility = View.GONE
        binding.recentConversationsRecyclerView.visibility = View.VISIBLE
        if (error != null) return@EventListener
        if (value != null) {
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val chatMessage = documentChange.toChatMessage()

                    // Verifique se a conversa já existe
                    val existingConversation = conversations.find {
                        it.conversionId == chatMessage.conversionId
                    }

                    if (existingConversation == null) {
                        conversations.add(chatMessage)
                    }
                } else if (documentChange.type == DocumentChange.Type.MODIFIED) {
                    updateConversation(documentChange)
                }
            }
            conversations.sortByDescending { it.dataObject }
            conversationsAdapter.notifyDataSetChanged()
            binding.recentConversationsRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun DocumentChange.toChatMessage(): ChatMessage {
        binding.layoutError.visibility = View.GONE
        binding.recentConversationsRecyclerView.visibility = View.VISIBLE
        val chatMessage = ChatMessage()
        val senderId = document.getString(Constants.KEY_SENDER_ID) ?: ""
        val receiverId = document.getString(Constants.KEY_RECEIVER_ID) ?: ""
        chatMessage.senderId = senderId
        chatMessage.receiverId = receiverId

        if (preferenceManager.getString(Constants.KEY_OBJ_USER) == senderId) {
            chatMessage.conversionImage = document.getString(Constants.KEY_RECEIVER_IMAGE) ?: ""
            chatMessage.conversionName = document.getString(Constants.KEY_RECEIVER_NAME) ?: ""
            chatMessage.conversionId = document.getString(Constants.KEY_RECEIVER_ID) ?: ""
        } else {
            chatMessage.conversionImage = document.getString(Constants.KEY_SENDER_IMAGE) ?: ""
            chatMessage.conversionName = document.getString(Constants.KEY_SENDER_NAME) ?: ""
            chatMessage.conversionId = document.getString(Constants.KEY_SENDER_ID) ?: ""
        }
        chatMessage.message = document.getString(Constants.KEY_LAST_MESSAGE) ?: ""
        chatMessage.dataObject = document.getDate(Constants.KEY_TIMESTAMP)!!
        return chatMessage
    }

    private fun updateConversation(documentChange: DocumentChange) {
        binding.layoutError.visibility = View.GONE
        binding.recentConversationsRecyclerView.visibility = View.VISIBLE
        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
        val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
        for (i in conversations.indices) {
            if (conversations[i].senderId == senderId && conversations[i].receiverId == receiverId) {
                conversations[i].apply {
                    message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE) ?: ""
                    dataObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!
                }
                break
            }
        }
    }

    override fun onConversionClicked(user: User) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        preferenceManager.putString(Constants.KEY_RECEIVER_ID, user.id.toString())
        intent.putExtra(Constants.KEY_OBJ_USER, user)
        startActivity(intent)
    }
}