package com.example.sekeni.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.sekeni.R
import com.example.sekeni.ui.onboarding.screens.OnboardingFirstScreen
import com.example.sekeni.ui.onboarding.screens.OnboardingSecondScreen
import com.example.sekeni.ui.onboarding.screens.OnboardingThirdScreen
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity

class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            OnboardingFirstScreen(),
            OnboardingSecondScreen(),
            OnboardingThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the ActionBar
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        // Hide the FloatingActionButton
        val fab: FloatingActionButton? = activity?.findViewById(R.id.fab)
        fab?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Show the ActionBar again
        (activity as? AppCompatActivity)?.supportActionBar?.show()

        // Show the FloatingActionButton again
        val fab: FloatingActionButton? = activity?.findViewById(R.id.fab)
        fab?.visibility = View.VISIBLE
    }
}
