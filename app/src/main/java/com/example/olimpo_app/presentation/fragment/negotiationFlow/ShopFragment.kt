package com.example.olimpo_app.presentation.fragment.negotiationFlow

import android.content.Intent
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
import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import com.example.olimpo_app.data.repository.AnnoucementRepository
import com.example.olimpo_app.databinding.FragmentShopBinding
import com.example.olimpo_app.presentation.activity.messageFlow.ChatActivity
import com.example.olimpo_app.presentation.adapters.AnnoucementAdapter
import com.example.olimpo_app.presentation.listeners.UserListener
import com.example.olimpo_app.presentation.ui.SpaceItemDecoration
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ShopFragment : Fragment(), UserListener {
    private lateinit var binding: FragmentShopBinding
    private lateinit var annoucementAdapter: AnnoucementAdapter
    private lateinit var preferenceManager: PreferenceManager
    private val featureApi = FeaturesApiInstance.service
    private val annoucementRepository = AnnoucementRepository(featureApi)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialize o RecyclerView uma vez
        annoucementAdapter = AnnoucementAdapter()
        binding.conversationsRecyclerView.apply {
            adapter = annoucementAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceItemDecoration(48))
        }

        // Configuração dos botões
        binding.btnVenda?.setOnClickListener {
            fetchData { annoucementRepository.getSalesAnnouncementsByCommunity("123") }
        }
        binding.btnServico?.setOnClickListener {
            fetchData { annoucementRepository.getServiceAnnouncementsByCommunity("123") }
        }
        binding.btnDoacao?.setOnClickListener {
            fetchData { annoucementRepository.getDonationsAnnouncementsByCommunity("123") }
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
            }
        }
    }

    private fun updateRecyclerView(posts: List<AnnouncementAPI>) {
        annoucementAdapter.postsList = posts
        annoucementAdapter.notifyDataSetChanged()
        binding.conversationsRecyclerView.visibility = View.VISIBLE
    }
    override fun onUserClicked(user: User, userAPI: UserAPI) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        preferenceManager.putString(Constants.KEY_RECEIVER_ID, user.apiId)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }
}