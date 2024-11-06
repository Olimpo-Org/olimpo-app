package com.example.olimpo_app.presentation.listeners

import java.util.UUID

interface RejectSolicitationListener {
    fun onRejectSolicitationClicked(solicitationId: UUID? = null)
}