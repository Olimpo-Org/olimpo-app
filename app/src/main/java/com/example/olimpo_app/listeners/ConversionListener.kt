package com.example.olimpo_app.listeners

import com.example.olimpo_app.data.models.User

interface ConversionListener {
    fun onConversionClicked(user: User)
}