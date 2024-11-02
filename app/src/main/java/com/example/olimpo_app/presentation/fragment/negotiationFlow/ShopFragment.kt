package com.example.olimpo_app.presentation.fragment.negotiationFlow

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
import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import com.example.olimpo_app.data.repository.AnnoucementRepository
import com.example.olimpo_app.databinding.FragmentShopBinding
import com.example.olimpo_app.presentation.adapters.AnnoucementAdapter
import com.example.olimpo_app.presentation.ui.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopFragment : Fragment() {
    private lateinit var binding: FragmentShopBinding
    private lateinit var annoucementAdapter: AnnoucementAdapter
    private val featureApi = FeaturesApiInstance.service
    private val annoucementRepository = AnnoucementRepository(featureApi)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnVenda?.setOnClickListener {
            fetchVenda()
        }
        binding.btnServico?.setOnClickListener {
            fetchService()
        }
        binding.btnDoacao?.setOnClickListener {
            fetchDonation()
        }
    }
        private fun fetchVenda() {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val posts = withContext(Dispatchers.IO) {
                        annoucementRepository.getSalesAnnouncementsByCommunity("123")
                    }
                    val postsList = posts.body()
                    setupRecyclerVendas(postsList ?: emptyList())
                    binding.conversationsRecyclerView.visibility = View.VISIBLE
                } catch (e: Exception) {
                    Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
                    Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
                    binding.conversationsRecyclerView.visibility = View.GONE
                }
            }
    }
    private fun setupRecyclerVendas(posts: List<AnnouncementAPI>) {
            annoucementAdapter = AnnoucementAdapter()
            annoucementAdapter.postsList = posts
            binding.conversationsRecyclerView.apply {
                adapter = annoucementAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(SpaceItemDecoration(48))
            }
    }
    private fun fetchService() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val posts = withContext(Dispatchers.IO) {
                    annoucementRepository.getServiceAnnouncementsByCommunity("123")
                }
                val postsList = posts.body()
                setupRecyclerService(postsList ?: emptyList())
                binding.conversationsRecyclerView.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
                binding.conversationsRecyclerView.visibility = View.GONE
            }
        }
    }
    private fun setupRecyclerService(posts: List<AnnouncementAPI>) {
        annoucementAdapter = AnnoucementAdapter()
        annoucementAdapter.postsList = posts
        binding.conversationsRecyclerView.apply {
            adapter = annoucementAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceItemDecoration(48))
        }
    }
    private fun fetchDonation() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val posts = withContext(Dispatchers.IO) {
                    annoucementRepository.getDonationsAnnouncementsByCommunity("123")
                }
                val postsList = posts.body()
                setupRecyclerDonation(postsList ?: emptyList())
                binding.conversationsRecyclerView.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
                binding.conversationsRecyclerView.visibility = View.GONE
            }
        }
    }
    private fun setupRecyclerDonation(posts: List<AnnouncementAPI>) {
        annoucementAdapter = AnnoucementAdapter()
        annoucementAdapter.postsList = posts
        binding.conversationsRecyclerView.apply {
            adapter = annoucementAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceItemDecoration(48))
        }
    }
}