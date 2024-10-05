package com.example.sekeni.ui.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sekeni.R
import com.example.sekeni.data.local.PreferencesHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OnboardingThirdScreen : Fragment() {
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Initialize preferencesHelper here
        preferencesHelper = PreferencesHelper(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_third_screen, container, false)
        val finishButton = view.findViewById<TextView>(R.id.finish)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        // Hide the FAB
        fab?.visibility = View.GONE
        finishButton.setOnClickListener {
            Log.d("OnboardingThirdScreen", "Finish button clicked.")
            preferencesHelper.setOnboardingFinished(true)
            // Delay navigation for 2 seconds
            view.postDelayed({
                findNavController().navigate(R.id.loginFragment)

            }, 2000) // 2000 milliseconds = 2 seconds
        }
        return view
    }
}
