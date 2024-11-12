package com.example.olimpo_app.presentation.activity.messageFlow

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.messageFlow.ChatMessage
import com.example.olimpo_app.databinding.ActivityChatMessageBinding
import com.example.olimpo_app.presentation.activity.BaseActivity
import com.example.olimpo_app.presentation.adapters.ChatAdapter
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Date
import java.util.Locale

class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private lateinit var receiverUser: User
    private lateinit var chatMessages: MutableList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore
    private var conversionId: String? = null
    private var isReceiverAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        loadReceiverDetails()
        init()
        listenMessages()
    }

    private fun listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_OBJ_USER))
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)

        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_OBJ_USER))
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            showToast("Erro ao carregar mensagens: ${error.message}")
            return@EventListener
        }

        if (value != null) {
            val count = chatMessages.size
            for (document in value.documentChanges) {
                if (document.type == DocumentChange.Type.ADDED) {
                    val chatMessage = ChatMessage(
                        document.document.getString(Constants.KEY_SENDER_ID)!!,
                        document.document.getString(Constants.KEY_RECEIVER_ID)!!,
                        document.document.getString(Constants.KEY_MESSAGE)!!,
                        getReadableDateTime(document.document.getDate(Constants.KEY_TIMESTAMP)!!),
                        document.document.getDate(Constants.KEY_TIMESTAMP)!!,
                        document.document.getString(Constants.KEY_OBJ_USER) ?: "",
                        document.document.getString(Constants.KEY_NAME) ?: "",
                        document.document.getString(Constants.KEY_IMAGE) ?: ""
                    )
                    chatMessages.add(chatMessage)
                }
            }

            chatMessages.sortWith { obj1, obj2 -> obj1.dataObject!!.compareTo(obj2.dataObject) }

            if (count == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.chatRecyclerView.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
        if (conversionId == null) {
            checkForConversion()
        }
    }

    private fun sendMessage() {
        val message = hashMapOf(
            Constants.KEY_SENDER_ID to preferenceManager.getString(Constants.KEY_OBJ_USER),
            Constants.KEY_RECEIVER_ID to receiverUser.id,
            Constants.KEY_MESSAGE to binding.inputMessage.text.toString(),
            Constants.KEY_TIMESTAMP to Date()
        )

        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        if (conversionId != null) {
            updateConversion(binding.inputMessage.text.toString())
        } else {
            val conversion: HashMap<String, Any> = HashMap()
            conversion[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_OBJ_USER) ?: ""
            conversion[Constants.KEY_SENDER_NAME] = preferenceManager.getString(Constants.KEY_NAME) ?: ""
            conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE) ?: ""
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.apiId
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name ?: ""
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser.image ?: ""
            conversion[Constants.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
            conversion[Constants.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }

        if (!isReceiverAvailable) {
            binding.inputMessage.text.toString()
        }

        binding.inputMessage.text = null
    }
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }



    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = mutableListOf()
        chatAdapter = ChatAdapter(
            chatMessages,
            receiverUser.image ?: "",
            preferenceManager.getString(Constants.KEY_OBJ_USER)!!
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }


    private fun loadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_OBJ_USER) as User
        binding.textName.text = receiverUser.name
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener { onBackPressed() }
        binding.layoutSend.setOnClickListener { sendMessage() }
    }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    private fun addConversion(conversion: HashMap<String, Any>) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { documentReference -> conversionId = documentReference.id }
    }

    private fun updateConversion(message: String) {
        val documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId.toString())
        documentReference.update(
            mapOf(
                Constants.KEY_LAST_MESSAGE to message,
                Constants.KEY_TIMESTAMP to Date()
            )
        )
    }

    private fun checkForConversion() {
        if (chatMessages.isNotEmpty()) {
            checkForConversionRemotely(
                preferenceManager.getString(Constants.KEY_OBJ_USER).toString(),
                receiverUser.apiId
            )
            checkForConversionRemotely(
                receiverUser.apiId,
                preferenceManager.getString(Constants.KEY_OBJ_USER).toString()
            )
        }
    }

    private fun checkForConversionRemotely(senderId: String, receiverId: String) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }

    private val conversionOnCompleteListener = OnCompleteListener<QuerySnapshot> { task ->
        if (task.isSuccessful && task.result != null && task.result!!.documents.isNotEmpty()) {
            val documentSnapshot = task.result!!.documents[0]
            conversionId = documentSnapshot.id
        }
    }

    override fun onResume() {
        super.onResume()
    }
}