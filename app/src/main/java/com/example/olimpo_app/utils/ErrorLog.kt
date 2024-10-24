package com.example.olimpo_app.utils

import android.util.Log

class ErrorLog {
    fun log(tag: String, e: Exception){
        Log.e(tag, "Error while creating user: ${e.message}")
        Log.e(tag, "Error: ${e.printStackTrace()}")
        Log.e(tag, "Error: ${e.localizedMessage}")
        Log.e(tag, "Error: ${e.cause}")
    }
}