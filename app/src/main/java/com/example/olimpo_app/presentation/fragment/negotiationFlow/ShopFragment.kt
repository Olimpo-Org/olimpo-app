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
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import com.example.olimpo_app.data.repository.AnnoucementRepository
import com.example.olimpo_app.databinding.FragmentShopBinding
import com.example.olimpo_app.presentation.adapters.AnnouncementAdapter
import com.example.olimpo_app.presentation.listeners.GoToConversationClicked
import com.example.olimpo_app.presentation.ui.SpaceItemDecoration
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.ObjectsLocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ShopFragment : Fragment(), GoToConversationClicked {
    private lateinit var binding: FragmentShopBinding
    private lateinit var annoucementAdapter: AnnouncementAdapter
    private val featureApi = FeaturesApiInstance.service
    private val annoucementRepository = AnnoucementRepository(featureApi)
    private var community: CommunityAPI? = null
    private val objectsLocalStorage = ObjectsLocalStorage()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        community = objectsLocalStorage.getObjectFromLocalStorage(
            requireActivity(),
            Constants.KEY_OBJ_COMMUNITY,
            CommunityAPI::class.java
        )

        // Inicialize o RecyclerView uma vez
        annoucementAdapter = AnnouncementAdapter(
            requireContext(),
            this

        )
        binding.conversationsRecyclerView.apply {
            adapter = annoucementAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceItemDecoration(48))
        }

        // Configuração dos botões
        binding.btnVenda.setOnClickListener {
            fetchData { annoucementRepository.getSalesAnnouncementsByCommunity(
                community?.id.toString()
            ) }
        }
        binding.btnServico.setOnClickListener {
            fetchData { annoucementRepository.getServiceAnnouncementsByCommunity(
                community?.id.toString()
            ) }
        }
        binding.btnDoacao.setOnClickListener {
            fetchData { annoucementRepository.getDonationsAnnouncementsByCommunity(
                community?.id.toString()
            ) }
        }
    }

    private fun fetchData(fetchFunction: suspend () -> Response<List<AnnouncementAPI>>) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val posts = withContext(Dispatchers.IO) { fetchFunction() }
                val postsList = posts.body() ?: emptyList()
                updateRecyclerView(postsList)
            } catch (e: Exception) {
                Log.e("ShopFragment", "Error fetching posts: ${e.message}")
                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
                binding.conversationsRecyclerView.visibility = View.GONE
                binding.layoutError.visibility = View.VISIBLE
            }
        }
    }

    private fun updateRecyclerView(posts: List<AnnouncementAPI>) {
        annoucementAdapter.postsList = posts
        annoucementAdapter.notifyDataSetChanged()
        binding.conversationsRecyclerView.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
    }

    override fun onGoToConversationClicked(userId: Int) {
        TODO("Not yet implemented")
    }
}