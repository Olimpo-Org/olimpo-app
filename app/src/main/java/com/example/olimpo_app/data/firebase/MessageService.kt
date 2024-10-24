package com.example.olimpo_app.data.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.example.olimpo_app.data.model.accessFlow.Community
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessageService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token: " + token)
    }

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message: " + remoteMessage.notification?.body)

        val user = User(
            name = remoteMessage.data[Constants.KEY_NAME]!!,
            id = remoteMessage.data[Constants.KEY_FIREBASE_USER_ID]!!,
            token = remoteMessage.data[Constants.KEY_FCM_TOKEN],
            )
        val community = Community(
            name = remoteMessage.data[Constants.KEY_COMMUNITY_NAME]!!,
            id = remoteMessage.data[Constants.KEY_COMMUNITY_ID]!!,
            token = remoteMessage.data[Constants.KEY_COMMUNITY_TOKEN],
        )
    }
}