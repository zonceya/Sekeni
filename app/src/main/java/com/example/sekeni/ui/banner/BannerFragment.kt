package com.example.sekeni.ui.banner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.sekeni.R


class BannerFragment : Fragment(R.layout.fragment_banner) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.bannerImageView)
        val imageRes = arguments?.getInt("imageRes")
        imageView?.setImageResource(imageRes ?: R.drawable.top_banner)
    }
}