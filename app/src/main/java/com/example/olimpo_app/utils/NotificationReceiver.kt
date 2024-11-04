package com.example.olimpo_app.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent){
        Toast.makeText(context, "Parabéns, publicação criada com sucesso🎉",  Toast.LENGTH_SHORT).show()
    }
}