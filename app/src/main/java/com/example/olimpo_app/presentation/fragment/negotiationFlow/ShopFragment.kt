package com.example.olimpo_app.presentation.fragment.negotiationFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.olimpo_app.FeaturesApiInstance
import com.example.olimpo_app.data.repository.AnnoucementRepository
import com.example.olimpo_app.databinding.FragmentShopBinding
import com.example.olimpo_app.presentation.adapters.PublicationAdapter

class ShopFragment : Fragment() {
    private lateinit var binding: FragmentShopBinding
    private lateinit var publicationAdapter: PublicationAdapter
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
    }
//        binding.btnVenda?.setOnClickListener {
//            fetchVenda()
//        }
//        binding.btnServico?.setOnClickListener {
//            fetchService()
//        }
//        binding.btnDoacao?.setOnClickListener {
//            fetchDonation()
//        }
//    }
//        private fun fetchVenda() {
//            viewLifecycleOwner.lifecycleScope.launch {
//                try {
//                    val posts = withContext(Dispatchers.IO) {
//                        annoucementRepository.getSalesAnnouncementsByCommunity()
//                    }
//                    val postsList = posts.body()
//                    setupRecyclerVendas(postsList ?: emptyList())
//                    binding.conversationsRecyclerView.visibility = View.VISIBLE
//                } catch (e: Exception) {
//                    Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
//                    Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
//                    binding.conversationsRecyclerView.visibility = View.GONE
//                }
//            }
//    }
//    private fun setupRecyclerVendas(posts: List<Publication>) {
//            publicationAdapter = PublicationAdapter()
//            publicationAdapter.postsList = posts
//            binding.conversationsRecyclerView.apply {
//                adapter = publicationAdapter
//                layoutManager = LinearLayoutManager(context)
//                addItemDecoration(SpaceItemDecoration(48))
//            }
//    }
//    private fun fetchService() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            try {
//                val posts = withContext(Dispatchers.IO) {
//                    annoucementRepository.getServiceAnnouncementsByCommunity()
//                }
//                val postsList = posts.body()
//                setupRecyclerService(postsList ?: emptyList())
//                binding.conversationsRecyclerView.visibility = View.VISIBLE
//            } catch (e: Exception) {
//                Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
//                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
//                binding.conversationsRecyclerView.visibility = View.GONE
//            }
//        }
//    }
//    private fun setupRecyclerService(posts: List<Publication>) {
//        publicationAdapter = PublicationAdapter()
//        publicationAdapter.postsList = posts
//        binding.conversationsRecyclerView.apply {
//            adapter = publicationAdapter
//            layoutManager = LinearLayoutManager(context)
//            addItemDecoration(SpaceItemDecoration(48))
//        }
//    }
//    private fun fetchDonation() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            try {
//                val posts = withContext(Dispatchers.IO) {
//                    annoucementRepository.getDonationsAnnouncementsByCommunity()
//                }
//                val postsList = posts.body()
//                setupRecyclerDonation(postsList ?: emptyList())
//                binding.conversationsRecyclerView.visibility = View.VISIBLE
//            } catch (e: Exception) {
//                Log.e("FeedFragment", "Error fetching posts | MESSAGE: ${e.message} | CAUSE: ${e.cause}")
//                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
//                binding.conversationsRecyclerView.visibility = View.GONE
//            }
//        }
//    }
//    private fun setupRecyclerDonation(posts: List<Publication>) {
//        publicationAdapter = PublicationAdapter()
//        publicationAdapter.postsList = posts
//        binding.conversationsRecyclerView.apply {
//            adapter = publicationAdapter
//            layoutManager = LinearLayoutManager(context)
//            addItemDecoration(SpaceItemDecoration(48))
//        }
//    }
}