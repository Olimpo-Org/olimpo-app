package com.example.olimpo_app.data.firebase

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageUpload {
    suspend fun uploadImage(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        val byteArrayOutputStream = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, this)
        }
        val dataBytes = byteArrayOutputStream.toByteArray()

        val storage = FirebaseStorage.getInstance()
        val ref = storage.getReference("gallery").child("img_of_olimpo_${System.currentTimeMillis()}.jpg")

        return@withContext try {
            ref.putBytes(dataBytes).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Log.d("FirebaseStorageManager", "Image uploaded successfully")
            downloadUrl
        } catch (e: Exception) {
            Log.d("FirebaseStorageManager", "Failed to upload image: ${e.message}")
            null
        }
    }
    suspend fun uploadImageList(bitmapList: List<Bitmap>): List<String> = withContext(Dispatchers.IO) {
        val imageList = mutableListOf<String>()
        bitmapList.forEach { bitmap ->
            val url = uploadImage(bitmap)
            if (url != null) {
                imageList.add(url)
            }
        }
        return@withContext imageList
    }
}