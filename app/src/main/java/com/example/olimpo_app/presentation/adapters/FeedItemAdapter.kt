package com.example.olimpo_app.presentation.adapters

import android.util.Log
import com.example.olimpo_app.data.model.feedFlow.AdvertisementAPI
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.google.gson.*
import java.lang.reflect.Type

class FeedItemAdapter : JsonDeserializer<FeedItem>, JsonSerializer<FeedItem> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FeedItem {
        val jsonObject = json.asJsonObject
        Log.d("FeedItemAdapter", "Deserializing: $jsonObject") // Log the JSON object being deserialized
        return if (jsonObject.has("senderName")) {
            val publication = context.deserialize<Publication>(jsonObject, Publication::class.java)
            FeedItem.PublicationItem(publication)
        } else if (jsonObject.has("title")) {
            val advertisement = context.deserialize<AdvertisementAPI>(jsonObject, AdvertisementAPI::class.java)
            FeedItem.AdvertisementItem(advertisement)
        } else {
            throw JsonParseException("Tipo de FeedItem desconhecido.")
        }
    }

    override fun serialize(src: FeedItem, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return when (src) {
            is FeedItem.PublicationItem -> context.serialize(src.publication)
            is FeedItem.AdvertisementItem -> context.serialize(src.advertisement)
        }
    }
}
