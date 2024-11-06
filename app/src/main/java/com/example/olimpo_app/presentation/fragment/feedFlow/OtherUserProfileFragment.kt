package com.example.olimpo_app.presentation.fragment.feedFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.olimpo_app.FeaturesApiInstance
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.repository.PublicationRepository
import com.example.olimpo_app.databinding.FragmentOtherUserProfileBinding
import com.example.olimpo_app.presentation.adapters.PublicationAdapter
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class OtherUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentOtherUserProfileBinding
    private val publicationRepository = PublicationRepository(FeaturesApiInstance.service)
    private val localStorage = ObjectsLocalStorage()
    private val adapter = PublicationAdapter()
    private var name: String? = null
    private var id: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentOtherUserProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            name = it.getString("name")
            id = it.getInt("id")
        }
        binding.username.text = name
    }

    private fun getPublication() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userApi = localStorage.getObjectFromLocalStorage(
                requireContext(),
                Constants.KEY_OBJ_COMMUNITY,
                CommunityAPI::class.java
            )
            val id = userApi?.id
            val response: Response<List<Publication>> =
                publicationRepository.getPublicationsByCommunityAndUser(
                    userApi?.id.toString(),
                    id.toString()
                )
            if (response.isSuccessful) {
                val publicationList = response.body()
                withContext(Dispatchers.Main) {
                    setupRecyclerView(publicationList!!)
                }
            }
        }
    }

    private fun setupRecyclerView(publicationList: List<Publication>) {
        adapter.postsList = publicationList
        binding.conversationsRecyclerView.adapter = adapter
    }
}