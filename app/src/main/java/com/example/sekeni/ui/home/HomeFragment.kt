package com.example.sekeni.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.sekeni.MainActivity
import com.example.sekeni.R
import com.example.sekeni.data.local.PreferencesHelper
import com.example.sekeni.ui.banner.BannerAdapter
import com.example.sekeni.ui.login.LoginViewModel
import com.example.sekeni.ui.product.ProductAdapter
import com.example.sekeni.ui.product.ProductViewModel
import com.facebook.AccessToken
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser

class HomeFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var divederview: View
    private lateinit var profileUsername: TextView
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewProductModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        viewProductModel = ViewModelProvider(this)[ProductViewModel::class.java]
        // Initialize views
        recyclerView = view.findViewById(R.id.displayProductRecyclerView)
        productAdapter = ProductAdapter(emptyList()) // Initialize with empty list
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = productAdapter

        // Observe ViewModel for product list
        viewProductModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateData(products)
        }
        val viewPager = view.findViewById<ViewPager2>(R.id.topBannerIcon)
        val bannerAdapter = BannerAdapter(this)
        viewPager.adapter = bannerAdapter
        autoScrollBanners(viewPager, bannerAdapter.bannerImages.size)
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.userProfileImage)
        profileName = headerView.findViewById(R.id.profileName)
        profileUsername =  headerView.findViewById(R.id.profileUsername)
        divederview = headerView.findViewById(R.id.divider)
        val name = homeViewModel.userName
        val profilePicUrl = homeViewModel.userProfilePicUrl
        val activity = requireActivity() as MainActivity
        activity.supportActionBar?.show()
        activity.binding.appBarMain.toolbar.title = getString(R.string.todo)
            // Update UI with fetched data
        updateUI(name, profilePicUrl)


        return view
    }
    private fun autoScrollBanners(viewPager: ViewPager2, itemCount: Int) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            var currentItem = 0

            override fun run() {
                currentItem = (currentItem + 1) % itemCount
                viewPager.currentItem = currentItem
                handler.postDelayed(this, 3000) // Scroll every 3 seconds
            }
        }
        handler.postDelayed(runnable, 3000)
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
        profileUsername.text = getString(R.string.profileUsername, name)
        loadProfileImage(profilePicUrl)
        profileImage.visibility = View.VISIBLE
        divederview.visibility = View.VISIBLE

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
