package com.example.olimpo_app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class JsonConverter {
    fun <T> saveObjectToJson(context: Context, key: String, obj: T) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val json = Gson().toJson(obj)

        editor.putString(key, json)
        editor.apply()
    }

    fun <T> getObjectFromJson(context: Context, key: String, classOfT: Class<T>): T? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(key, null)

        return if (json != null) {
            Gson().fromJson(json, classOfT)
        } else {
            null
        }
    }
}