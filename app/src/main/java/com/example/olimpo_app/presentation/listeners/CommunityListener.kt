package com.example.olimpo_app.presentation.listeners

import com.example.olimpo_app.data.model.accessFlow.Community

interface CommunityListener {
    fun onCommunityClicked(community: Community)
}