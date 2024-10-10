package com.example.sekeni.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.sekeni.R
import com.example.sekeni.data.local.PreferencesHelper
import com.example.sekeni.ui.login.LoginViewModel
import com.facebook.AccessToken
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser

class HomeFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        // Initialize views
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.userProfileImage)
        profileName = headerView.findViewById(R.id.profileName)
        val name = homeViewModel.userName
        val profilePicUrl = homeViewModel.userProfilePicUrl

        // Update UI with fetched data
        updateUI(name, profilePicUrl)

        return view
    }
    private fun updateUI(name: String?, profilePicUrl: String?) {
        // Check if name and profilePicUrl are valid
        if (name.isNullOrEmpty() || profilePicUrl.isNullOrEmpty()) {
            Log.e("HomeFragment", "Name or Profile Picture is missing")
            // Handle the error (e.g., show a default image or prompt the user)
            return
        }

        //showLoadingIndicator()

        profileName.text = name
        profileName.visibility = View.VISIBLE

        loadProfileImage(profilePicUrl)
        profileImage.visibility = View.VISIBLE

       // hideLoadingIndicator()
    }

    private fun loadProfileImage(profilePicUrl: String) {
        val requestOptions = RequestOptions()
            .override(50, 50)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        try {
            Glide.with(this)
                .load(profilePicUrl)
                .apply(requestOptions)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.rectangular)
                .into(profileImage)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error loading image", e)
        }
    }

    private fun showLoadingIndicator() {
        loadingIndicator.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
    }
}
