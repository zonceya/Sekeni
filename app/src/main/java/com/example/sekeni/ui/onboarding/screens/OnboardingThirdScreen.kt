package com.example.sekeni.ui.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sekeni.R

class OnboardingThirdScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_third_screen, container, false)
        val finishButton = view.findViewById<TextView>(R.id.finish)
        finishButton.setOnClickListener {
            onBoardingFinished()
            findNavController().navigate(R.id.action_viewPagerFragment_to_loginFragment)
        }
        return view
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("Finished", true)
            apply()
        }
    }
}
