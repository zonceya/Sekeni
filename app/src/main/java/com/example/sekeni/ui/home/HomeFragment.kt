package com.example.sekeni.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
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
import com.example.sekeni.ui.product.ProductAdapter
import com.example.sekeni.ui.product.ProductViewModel
import com.google.android.material.navigation.NavigationView
import androidx.navigation.fragment.findNavController
import com.example.sekeni.ui.category.CategoryAdapter
import com.example.sekeni.ui.category.CategoryViewModel
import androidx.appcompat.app.ActionBarDrawerToggle

class HomeFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var divederview: View
    private lateinit var profileUsername: TextView
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerCategoryView: RecyclerView
    private lateinit var viewProductModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewCategoryModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private var currentPage = 1
    private var isLoading = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        viewProductModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)

        viewCategoryModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)
        // Initialize the adapter before setting it to RecyclerView
        productAdapter = ProductAdapter(emptyList()) { productId ->
            viewProductModel.selectProduct(productId.toString())
            findNavController().navigate(R.id.action_homeFragment_to_productFragment)
        }
        categoryAdapter = CategoryAdapter(emptyList()) { categoryId ->
            viewCategoryModel.selectCategory(categoryId.toString())

        }
        recyclerView = view.findViewById(R.id.displayProductRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = productAdapter

        recyclerCategoryView = view.findViewById(R.id.newListRecyclerView)
        recyclerCategoryView.layoutManager = GridLayoutManager(context, 2)
        recyclerCategoryView.adapter = categoryAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // Trigger pagination when reaching near the bottom
                if (!isLoading && lastVisibleItem >= totalItemCount - 2) {
                    viewProductModel.fetchNextPage()
                }
            }
        })
        // Observe ViewModel for product list and update adapter
        viewProductModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateData(products)
        }
        viewCategoryModel.category.observe(viewLifecycleOwner) { category ->
            categoryAdapter.updateData(category)
        }


        recyclerCategoryView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // Trigger pagination when reaching near the bottom
                if (!isLoading && lastVisibleItem >= totalItemCount - 2) {
                    viewCategoryModel.fetchNextPage()
                }
            }
        })
        val viewPager = view.findViewById<ViewPager2>(R.id.topBannerIcon)
        val bannerAdapter = BannerAdapter(this)
        viewPager.adapter = bannerAdapter
        autoScrollBanners(viewPager, bannerAdapter.bannerImages.size)

        val navigationView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.userProfileImage)
        profileName = headerView.findViewById(R.id.profileName)
        profileUsername = headerView.findViewById(R.id.profileUsername)
        divederview = headerView.findViewById(R.id.divider)

        val name = homeViewModel.userName
        val profilePicUrl = homeViewModel.userProfilePicUrl
        val activity = requireActivity() as MainActivity
        activity.supportActionBar?.show()

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
        if (name.isNullOrEmpty() || profilePicUrl.isNullOrEmpty()) {
            Log.e("HomeFragment", "Name or Profile Picture is missing")
            return
        }

        profileName.text = name
        profileName.visibility = View.VISIBLE
        profileUsername.text = getString(R.string.profileUsername, name)
        loadProfileImage(profilePicUrl)
        profileImage.visibility = View.VISIBLE
        divederview.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()

        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        activity.setSupportActionBar(toolbar)

        activity.supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(false) // Disable back button
            setHomeButtonEnabled(true) // No back button in Home
        }

        // Unlock drawer in HomeFragment
        val drawerLayout = activity.findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val toggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
}

