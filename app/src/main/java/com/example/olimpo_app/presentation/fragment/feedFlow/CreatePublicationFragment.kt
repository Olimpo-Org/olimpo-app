package com.example.olimpo_app.presentation.fragment.feedFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.olimpo_app.R
import com.example.olimpo_app.databinding.FragmentCreatePublicationBinding

class CreatePublicationFragment : Fragment() {
    private lateinit var binding: FragmentCreatePublicationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentCreatePublicationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_publication, container, false)
    }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
        }
}