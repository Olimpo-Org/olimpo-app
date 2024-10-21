package com.example.olimpo_app.presentation.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.olimpo_app.data.models.Publication
import com.example.olimpo_app.presentation.usecase.PublicationUseCase
import kotlinx.coroutines.launch

class GetAllPostsViewModel(
    private val publicationUseCase: PublicationUseCase
) : ViewModel() {
    val posts = MutableLiveData<List<Publication>>()
    fun getPosts() {
        viewModelScope.launch {
            try {
                val result = publicationUseCase.getAllPosts()
                posts.value = result.body()
//                Log.d("PostsViewModel", "getPosts result: ${posts.value }")
            } catch (e: Exception) {
                Log.e("PostsViewModel", "Error fetching todos | MESSAGE ${e.message} | CAUSE ${e.cause}")
            }
        }
    }
}