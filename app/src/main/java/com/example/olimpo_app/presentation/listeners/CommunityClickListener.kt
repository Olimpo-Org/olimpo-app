package com.example.olimpo_app.presentation.listeners

import com.example.olimpo_app.data.model.accessFlow.Community
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI

interface CommunityClickListener {
    fun onCommunityClicked(community: Community, communityAPI: CommunityAPI)
}