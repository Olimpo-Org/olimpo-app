package com.example.olimpo_app.presentation.fragment.accessFlow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.olimpo_app.FeaturesApiInstance
import com.example.olimpo_app.R
import com.example.olimpo_app.data.repository.PublicationRepository
import com.example.olimpo_app.databinding.FragmentUserProfileBinding
import com.example.olimpo_app.presentation.adapters.PublicationAdapter
import com.example.olimpo_app.presentation.ui.SpaceItemDecoration
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var publicationAdapter: PublicationAdapter

    private val featureApi = FeaturesApiInstance.service
    private val publicationRepository = PublicationRepository(featureApi)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding class
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize preferenceManager here after the view is created
        preferenceManager = PreferenceManager(requireContext())



        // Load the user details after the view is fully created
        loadUserDetails()
    }

    private fun fetchPublication() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val posts = withContext(Dispatchers.IO) {
                    publicationRepository.getPublicationsByCommunity("123")
                }
                val postsList = posts.body()
                setupRecycler(postsList ?: emptyList())
                binding.conversationsRecyclerView.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
                binding.conversationsRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setupRecycler(posts: List<Object>) {
        publicationAdapter = PublicationAdapter()
        publicationAdapter.postsList = posts
        binding.conversationsRecyclerView.apply {
            adapter = publicationAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceItemDecoration(48))
        }
    }

    private fun loadUserDetails() {
        // Update UI elements using binding
        binding.username.text = preferenceManager.getString(Constants.KEY_NAME)
        Glide.with(this)
            .load(Constants.KEY_IMAGE)
            .override(1800, 1800)
            .placeholder(R.drawable.placeholder_image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.userProfile)
    }
}