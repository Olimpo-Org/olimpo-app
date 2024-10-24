package com.example.olimpo_app.presentation.activity.messageFlow

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.example.olimpo_app.data.model.messageFlow.ChatMessage
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.databinding.ActivityChatMessageBinding
import com.example.olimpo_app.presentation.activity.BaseActivity
import com.example.olimpo_app.presentation.adapters.ChatAdapter
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.json.JSONArray
import org.json.JSONObject
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
        setListners()
        loadReceiverDetails()
        init()
        listenMessages()

    }

    private fun listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener  = EventListener<QuerySnapshot>{ value: QuerySnapshot?, error:Throwable? ->
        if(error != null){

        }
        if(value != null){
            val count = chatMessages.size
            for (document in value.documentChanges){
                if(document.type == DocumentChange.Type.ADDED){
                    val chatMessage = ChatMessage(
                        document.document.getString(Constants.KEY_SENDER_ID)!!,
                        document.document.getString(Constants.KEY_RECEIVER_ID)!!,
                        document.document.getString(Constants.KEY_MESSAGE)!!,
                        getReadableDateTime(document.document.getDate(Constants.KEY_TIMESTAMP)!!),
                        document.document.getDate(Constants.KEY_TIMESTAMP)!!,
                        document.document.getString(Constants.KEY_FIREBASE_USER_ID)?: "",
                        document.document.getString(Constants.KEY_NAME)?: "",
                        document.document.getString(Constants.KEY_IMAGE)?: ""
                    )
                    chatMessages.add(chatMessage)
                }
            }
            chatMessages.sortWith { obj1: ChatMessage, obj2: ChatMessage ->
                obj1.dataObject!!.compareTo(
                    obj2.dataObject
                )
            }

            if (count == 0){
                chatAdapter.notifyDataSetChanged()
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.chatRecyclerView.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
        if(conversionId == null){
            checkForConversion()
        }
    }

    private fun sendMessage(){
        val message = hashMapOf(
            Constants.KEY_SENDER_ID to preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID),
            Constants.KEY_RECEIVER_ID to preferenceManager.getString(Constants.KEY_RECEIVER_ID),
            Constants.KEY_MESSAGE to binding.inputMessage.text.toString(),
            Constants.KEY_TIMESTAMP to Date()
        )
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        if(conversionId != null) {
            updateConversion(binding.inputMessage.text.toString())
        }else{
            val conversion: HashMap<String, Any> = HashMap()
            conversion[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID) ?: ""
            conversion[Constants.KEY_SENDER_NAME] = preferenceManager.getString(Constants.KEY_NAME) ?: ""
            conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE) ?: ""
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.id
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name ?: ""
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser.image ?: ""
            conversion[Constants.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
            conversion[Constants.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }
        if(!isReceiverAvailable){
            try{
                val tokens = JSONArray()
                tokens.put(receiverUser.token)

                val data = JSONObject()
                data.put(Constants.KEY_FIREBASE_USER_ID, preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID))
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME))
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN))
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.text.toString())

                val body = JSONObject()
                body.put(Constants.REMOTE_MSG_DATA, data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)
            }catch (e: Exception){
                showToast(e.message.toString())
            }
        }
        binding.inputMessage.text = null
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
    private fun listenAvailabilityOfReceiver() {
        database.collection(Constants.KEY_COLLECTION_USERS).document(receiverUser.id)
            .addSnapshotListener(this) { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    value.getLong(Constants.KEY_AVAILABILITY)?.let {
                        val availability = it.toInt()
                        isReceiverAvailable = availability == 1
                    }
                    receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN)
                    if(receiverUser.image == null){
                        receiverUser.image = value.getString(Constants.KEY_IMAGE)
                        chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.image!!))
                        chatAdapter.notifyItemRangeChanged(0, chatMessages.size)
                    }
                }
                if(isReceiverAvailable){
                    binding.textAvailability.visibility = View.VISIBLE
                }else{
                    binding.textAvailability.visibility = View.GONE
                }
            }
    }

    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = emptyList<ChatMessage>().toMutableList()
        chatAdapter = ChatAdapter(
            chatMessages,
            getBitmapFromEncodedString(receiverUser.image!!),
            preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID)!!
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap {
        return if(encodedImage != null){
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else{
            null!!
        }
    }

    private fun loadReceiverDetails() = with(binding){
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        textName.text = receiverUser.name
    }

    private fun setListners() = with(binding){
        imageBack.setOnClickListener{ onBackPressedDispatcher.onBackPressed() }
        layoutSend.setOnClickListener{ sendMessage() }
    }

    private fun getReadableDateTime(date: Date): String{
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }
    private fun addConversion(conversion: HashMap<String, Any>) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { documentReference -> conversionId = documentReference.id }
    }

    private fun updateConversion(message: String) {
        val documentReference: DocumentReference =
            database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId.toString())
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
                preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID).toString(),
                receiverUser.id
            )
            checkForConversionRemotely(
                receiverUser.id,
                preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID).toString()
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
        listenAvailabilityOfReceiver()
    }
}
