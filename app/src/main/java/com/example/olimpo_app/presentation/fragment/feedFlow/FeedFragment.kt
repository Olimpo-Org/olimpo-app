package com.example.olimpo_app.presentation.fragment.feedFlow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olimpo_app.FeaturesApiInstance
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.repository.PublicationRepository
import com.example.olimpo_app.databinding.FragmentFeedBinding
import com.example.olimpo_app.presentation.adapters.FeedAdapter
import com.example.olimpo_app.presentation.adapters.FeedItem
import com.example.olimpo_app.presentation.listeners.OnLikeClicked
import com.example.olimpo_app.presentation.listeners.OnUserNameClicked
import com.example.olimpo_app.presentation.ui.SpaceItemDecoration
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedFragment : Fragment(), OnLikeClicked, OnUserNameClicked {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var feedAdapter: FeedAdapter

    private val featureApi = FeaturesApiInstance.service
    private val publicationRepository = PublicationRepository(featureApi)
    private val objectsLocalStorage = ObjectsLocalStorage()
    private var userId: Int? = null
    private var communityId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userApi = objectsLocalStorage.getObjectFromLocalStorage(
            requireContext(),
            Constants.KEY_OBJ_USER,
            UserAPI::class.java
        )
        val communityApi = objectsLocalStorage.getObjectFromLocalStorage(
            requireContext(),
            Constants.KEY_OBJ_COMMUNITY,
            CommunityAPI::class.java
        )
        communityId = communityApi?.id
        userId = userApi?.id
        fetchPublication()
    }

    private fun fetchPublication() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("FeedFragment", "Fetching posts, communityId: $communityId")
                val response = withContext(Dispatchers.IO) {
                    publicationRepository.getPublicationsByCommunity(
                        communityId.toString()
                    )
                }
                Log.d("FeedFragment", "Response: $response")
                val postsList = response.body() ?: emptyList()

                setupRecycler(postsList)
                binding.conversationsRecyclerView.visibility = View.VISIBLE
                binding.layoutError.visibility = View.GONE
            } catch (e: Exception) {
                Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
                binding.layoutError.visibility = View.VISIBLE
                binding.conversationsRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setupRecycler(feedItems: List<FeedItem>) {
        try {
            feedAdapter = userId?.let {
                FeedAdapter(feedItems, this, this, it)
            }!!

            binding.conversationsRecyclerView.apply {
                adapter = feedAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(SpaceItemDecoration(48))
            }
        } catch (e: Exception) {
            Log.e("FeedFragment", "Error setting up recycler | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
        }
    }

    override fun onLikeClicked(
        publicationId: String,
        userId: Int
    ) {
        for (item in feedAdapter.itemList) {
            if (item is FeedItem.PublicationItem) {
                if (item.publication.publicationId == publicationId) {
                    if (item.publication.likes?.contains(userId.toString()) == true) {
                        item.publication.likes.remove(userId.toString())
                        lifecycleScope.launch {
                            publicationRepository.unlikePublication(publicationId, userId.toString())
                        }
                    } else {
                        item.publication.likes?.add(userId.toString())
                        lifecycleScope.launch {
                            publicationRepository.likePublication(publicationId, userId.toString())
                        }
                    }
                }
            }
        }
        feedAdapter.notifyDataSetChanged()
    }

    override fun onUserNameClicked(userId: Int, userName: String) {
        val fragment = OtherUserProfileFragment()
        val bundle = Bundle()
        bundle.putInt("userId", userId)
        bundle.putString("userName", userName)
        fragment.arguments = bundle
        val parentFragmentManager = parentFragmentManager

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()

    }
}
