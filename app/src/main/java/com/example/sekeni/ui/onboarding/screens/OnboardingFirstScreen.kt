package com.example.sekeni.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.sekeni.R

class OnboardingFirstScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding_first_screen, container, false)

        // Assuming ViewPager2 is part of the activity's layout
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        val nextButton = view.findViewById<TextView>(R.id.next)

        // Handle button click to navigate to the next screen
        nextButton.setOnClickListener {
            if (viewPager != null) {
                viewPager.currentItem += 1
            }  // Move to next page
        }

        return view
    }

}
