package com.example.olimpo_app.presentation.listeners

import java.util.UUID

interface SolicitListener {
    fun onSolicitClicked(
        communityId: UUID,
    )
}