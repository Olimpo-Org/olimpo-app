package com.example.olimpo_app.presentation.activities.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.olimpo_app.R
import com.example.olimpo_app.databinding.FragmentListCommunitiesChatsBinding

class ListCommunitiesChatsFragment : Fragment() {
    private lateinit var binding: FragmentListCommunitiesChatsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentListCommunitiesChatsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_communities_chats, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}