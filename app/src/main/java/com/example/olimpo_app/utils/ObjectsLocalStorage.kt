package com.example.olimpo_app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class ObjectsLocalStorage {
    fun <T> saveObjectInLocalStorage(context: Context, key: String, obj: T) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val json = Gson().toJson(obj)

        editor.putString(key, json)
        editor.apply()
    }

    fun <T> getObjectFromLocalStorage(context: Context, key: String, classOfT: Class<T>): T? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(key, null)

        return if (json != null) {
            Gson().fromJson(json, classOfT)
        } else {
            null
        }
    }
    fun cleanObjectFromLocalStorage(context: Context, key: String): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(key)
        return editor.commit()
    }
}