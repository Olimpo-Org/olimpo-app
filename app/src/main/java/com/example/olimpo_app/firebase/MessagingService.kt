package com.example.olimpo_app.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.example.olimpo_app.models.User
import com.example.olimpo_app.utilites.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService: FirebaseMessagingService() {

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
            id = remoteMessage.data[Constants.KEY_USER_ID]!!,
            token = remoteMessage.data[Constants.KEY_FCM_TOKEN],

            )
    }
}