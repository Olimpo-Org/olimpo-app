package com.example.olimpo_app.presentation.fragment.feedFlow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.R
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.databinding.FragmentFeedBinding
import com.example.olimpo_app.presentation.adapters.PublicationAdapter
import com.example.olimpo_app.presentation.ui.SpaceItemDecoration

class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var publicationAdapter: PublicationAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = FragmentFeedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {

        } catch (e: Exception) {
            Log.e("PostsViewModel", "Error fetching todos | MESSAGE ${e.message} | CAUSE ${e.cause}")
            Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
            binding.layoutError.visibility = View.VISIBLE
        }
    }
    private fun setupRecycler(posts: List<Publication>) = binding.conversationsRecyclerView.apply {
        val postsAdapter = PublicationAdapter()
        postsAdapter.postsList = posts
        adapter = postsAdapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(SpaceItemDecoration(48))
    }
}