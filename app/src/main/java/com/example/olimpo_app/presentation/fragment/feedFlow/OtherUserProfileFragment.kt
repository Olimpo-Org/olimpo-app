package com.example.olimpo_app.presentation.fragment.feedFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olimpo_app.FeaturesApiInstance
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.repository.PublicationRepository
import com.example.olimpo_app.databinding.FragmentOtherUserProfileBinding
import com.example.olimpo_app.presentation.activity.messageFlow.ChatActivity
import com.example.olimpo_app.presentation.adapters.PublicationAdapter
import com.example.olimpo_app.presentation.listeners.ConversionListener
import com.example.olimpo_app.presentation.ui.SpaceItemDecoration
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import com.example.olimpo_app.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class OtherUserProfileFragment : Fragment(), ConversionListener {
    private lateinit var binding: FragmentOtherUserProfileBinding
    private val publicationRepository = PublicationRepository(FeaturesApiInstance.service)
    private val localStorage = ObjectsLocalStorage()
    private var adapter = PublicationAdapter()
    private lateinit var preferenceManager: PreferenceManager
    private var name: String? = null
    private var id: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferenceManager = PreferenceManager(requireActivity())
        binding = FragmentOtherUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        id = preferenceManager.getString(Constants.anotherUserProfileId)
        name = preferenceManager.getString(Constants.anotherUserProfileName)
        super.onViewCreated(view, savedInstanceState)
        binding.username.text = name
        getPublication()
    }

    private fun getPublication() {
        lifecycleScope.launch(Dispatchers.IO) {
            val communityAPI = localStorage.getObjectFromLocalStorage(
                requireContext(),
                Constants.KEY_OBJ_COMMUNITY,
                CommunityAPI::class.java
            )
            Log.d(
                "communityAPI",
                "getPublication: $communityAPI --- $id"
            )
            val response: Response<List<Publication>> =
                publicationRepository.getPublicationsByCommunityAndUser(
                    communityAPI?.id.toString(),
                    id.toString()
                )
            Log.d("response", "getPublication: $response")
            if (response.isSuccessful) {
                val publicationList = response.body()
                withContext(Dispatchers.Main) {
                    setupRecyclerView(publicationList!!)
                }
            }
        }
    }

    private fun setupRecyclerView(publicationList: List<Publication>) {
        try {
            adapter = PublicationAdapter()
            adapter.publicationList = publicationList
            binding.conversationsRecyclerView.apply {
                adapter = adapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(SpaceItemDecoration(48))
            }
        } catch (e: Exception) {
            Log.e(
                "FeedFragment",
                "Error setting up recycler | MESSAGE: ${e.message} | CAUSE: ${e.cause}"
            )
        }
    }
    override fun onConversionClicked(user: User) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        preferenceManager.putString(Constants.KEY_RECEIVER_ID, user.id.toString())
        intent.putExtra(Constants.KEY_OBJ_USER, user)
        startActivity(intent)
    }
}