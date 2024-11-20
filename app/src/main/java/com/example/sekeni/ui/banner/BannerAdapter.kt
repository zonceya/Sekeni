package com.example.sekeni.ui.banner

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sekeni.R

class BannerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    val bannerImages = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
    )

    override fun getItemCount(): Int = bannerImages.size

    override fun createFragment(position: Int): Fragment {
        val fragment = BannerFragment()
        val bundle = Bundle()
        bundle.putInt("imageRes", bannerImages[position])
        fragment.arguments = bundle
        return fragment
    }
}