package com.example.olimpo_app.data.firebase

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageUpload {
    suspend fun uploadImage(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            val dataBytes = Base64.decode(base64Image, Base64.DEFAULT)

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
        } catch (e: Exception) {
            Log.d("FirebaseStorageManager", "Error: ${e.message}")
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