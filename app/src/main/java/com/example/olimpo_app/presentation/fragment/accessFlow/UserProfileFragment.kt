package com.example.olimpo_app.presentation.fragment.accessFlow

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        val imageString = preferenceManager.getString(Constants.KEY_IMAGE)
        if (!imageString.isNullOrEmpty()) {
            val bytes = Base64.decode(imageString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.userProfile.setImageBitmap(bitmap)
        }
    }
}