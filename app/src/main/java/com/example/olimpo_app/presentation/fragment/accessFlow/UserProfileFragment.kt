package com.example.olimpo_app.presentation.fragment.accessFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.olimpo_app.R
import com.example.olimpo_app.databinding.FragmentUserProfileBinding
import com.example.olimpo_app.utils.Constants
import com.example.olimpo_app.utils.PreferenceManager

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var preferenceManager: PreferenceManager

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