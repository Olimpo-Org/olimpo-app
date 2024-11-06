package com.example.olimpo_app.presentation.adapters

import com.example.olimpo_app.data.model.feedFlow.AdvertisementAPI
import com.example.olimpo_app.data.model.feedFlow.Publication

sealed class FeedItem {
    data class PublicationItem(val publication: Publication) : FeedItem()
    data class AdvertisementItem(val advertisement: AdvertisementAPI) : FeedItem()
}
