package com.example.olimpo_app.data.model.feedFlow

import java.time.LocalDate

data class AdvertisementAPI(
    val id: Long? = null,
    val title: String,
    val description: String,
    val divulgationDate: LocalDate,
    val category: Int? = null,
    val imageUrl: String? = null,
    val userId: Long? = null,
    val idPlan: Long? = null
)
