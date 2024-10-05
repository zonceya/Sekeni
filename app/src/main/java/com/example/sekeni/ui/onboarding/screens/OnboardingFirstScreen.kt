package com.example.sekeni.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.sekeni.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OnboardingFirstScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding_first_screen, container, false)

        // Assuming ViewPager2 is part of the activity's layout
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)

        // Hide the FAB
        fab?.visibility = View.GONE
        // Navigate to the next screen after a delay


        return view
    }

}
