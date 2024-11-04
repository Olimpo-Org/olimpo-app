package com.example.olimpo_app.presentation.listeners

import java.util.UUID

interface AcceptSolicitationListener {
    fun onAcceptSolicitationClicked(solicitationId: UUID? = null)
}