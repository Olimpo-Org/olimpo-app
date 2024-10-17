package com.example.olimpo_app.presentation.activities.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.olimpo_app.R
import com.example.olimpo_app.databinding.FragmentFeedBinding
import com.example.olimpo_app.presentation.adapters.PublicationAdapter

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

        recyclerView = binding.conversationsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        publicationAdapter = PublicationAdapter()
        recyclerView.adapter = publicationAdapter


    }
}