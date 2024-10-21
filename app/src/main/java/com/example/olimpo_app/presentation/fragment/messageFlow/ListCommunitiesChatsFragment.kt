package com.example.olimpo_app.presentation.fragment.messageFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.messageFlow.ChatMessage
import com.example.olimpo_app.databinding.FragmentListCommunitiesChatsBinding
import com.example.olimpo_app.presentation.listeners.ConversionListener
import com.example.olimpo_app.presentation.adapters.RecentConversationsAdapter
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging

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
        getToken()
        listenConversations()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEncontrarPessoas.setOnClickListener {
            val fragment = FindChatFragment()
            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    private fun init(){
        conversations = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversations, this)
        database = FirebaseFirestore.getInstance()
    }

    private fun listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            return@EventListener
        }
        if (value != null) {
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID) ?: ""
                    val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID) ?: ""
                    val chatMessage = ChatMessage()
                    chatMessage.senderId = senderId
                    chatMessage.receiverId = receiverId
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE) ?: ""
                        chatMessage.conversionName = documentChange.document.getString(Constants.KEY_RECEIVER_NAME) ?: ""
                        chatMessage.conversionId = documentChange.document.getString(Constants.KEY_RECEIVER_ID) ?: ""
                    }else{
                        chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_SENDER_IMAGE) ?: ""
                        chatMessage.conversionName = documentChange.document.getString(Constants.KEY_SENDER_NAME) ?: ""
                        chatMessage.conversionId = documentChange.document.getString(Constants.KEY_SENDER_ID) ?: ""
                    }
                    chatMessage.message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE) ?: ""
                    chatMessage.dataObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!
                    conversations.add(chatMessage)
                }else if(documentChange.type == DocumentChange.Type.MODIFIED){
                    for (i in conversations.indices) {
                        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        if (conversations[i].senderId == senderId && conversations[i].receiverId == receiverId) {
                            conversations[i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE) ?: ""
                            conversations[i].dataObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!
                            break
                        }
                    }
                }
            }
            conversations.sortWith { obj1, obj2 -> obj2.dataObject!!.compareTo(obj1.dataObject!!) }
            conversationsAdapter.notifyDataSetChanged()
            binding.recentConversationsRecyclerView.smoothScrollToPosition(0)
            binding.recentConversationsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun getToken(){
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { updateToken(it) }
    }

    private fun updateToken(token: String){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token)
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USER_ID)!!)
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener { }

    }
}