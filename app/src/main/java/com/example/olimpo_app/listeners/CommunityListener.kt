package com.example.olimpo_app.listeners

import com.example.olimpo_app.data.models.Community

interface CommunityListener {
    fun onCommunityClicked(community: Community)
}