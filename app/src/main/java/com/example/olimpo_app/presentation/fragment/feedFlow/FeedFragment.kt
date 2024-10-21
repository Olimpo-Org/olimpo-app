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
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.repository.PublicationRepository
import com.example.olimpo_app.databinding.FragmentFeedBinding
import com.example.olimpo_app.presentation.adapters.PublicationAdapter
import com.example.olimpo_app.presentation.ui.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var publicationAdapter: PublicationAdapter

    private val featureApi = FeaturesApiInstance.api
    private val publicationRepository = PublicationRepository(featureApi)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchPosts()
    }

    private fun fetchPosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val posts = withContext(Dispatchers.IO) {
                    publicationRepository.getAllPosts()
                }
                val postsList = posts.body()
                setupRecycler(postsList ?: emptyList())
            } catch (e: Exception) {
                Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
                binding.layoutError.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecycler(posts: List<Publication>) {
        publicationAdapter = PublicationAdapter()
        publicationAdapter.postsList = posts
        binding.conversationsRecyclerView.apply {
            adapter = publicationAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceItemDecoration(48))
        }
    }
}
