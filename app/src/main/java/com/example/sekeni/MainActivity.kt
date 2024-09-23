package com.example.sekeni

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.sekeni.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Thread.sleep(3000)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        //clearOnboardingPreferences()
        // Check if onboarding is finished
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val onboardingFinished = sharedPref.getBoolean("Finished", false)
        // Set up navigation
        val navController = findNavController(R.id.nav_host_fragment)

        if (!onboardingFinished) {
            // Navigate to onboarding screen if it hasn't finished
            navController.navigate(R.id.viewPagerFragment)
        } else {
            // Onboarding finished, now check login status
            val isLoggedIn = sharedPref.getBoolean("LoggedIn", false)
            if (isLoggedIn) {
                // Navigate to home screen if logged in
                navController.navigate(R.id.nav_home)
            } else {
                // Navigate to login screen if not logged in
                navController.navigate(R.id.loginFragment)
            }
        }
        // Set up navigation drawer
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
       // navView.setNavigationItemSelectedListener(this)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initialize Facebook SDK
        auth = FirebaseAuth.getInstance()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            performLogout()
            true
        }
    }
    private fun clearOnboardingPreferences() {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

   private fun performLogout() {
        // Sign out from Firebase
        Log.d("MainActivity", "Signing out from Firebase")
        auth.signOut()

        // Sign out from Facebook
        Log.d("MainActivity", "Logging out from Facebook")
        LoginManager.getInstance().logOut()

        // Update shared preferences
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("LoggedIn", false)
            apply()
        }
       Log.d("MainActivity", "Signing out from Google")
       googleSignInClient.signOut().addOnCompleteListener(this) {
           // Update shared preferences
           val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
           with(sharedPref.edit()) {
               putBoolean("LoggedIn", false)
               apply()
           }
       }
        // Navigate to login screen
        Log.d("MainActivity", "Navigating to login screen")
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.loginFragment)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // Check if the drawer is open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (viewPager != null && viewPager.currentItem > 0) {
            // If the ViewPager has more pages, navigate back
            viewPager.currentItem -= 1
        } else {
            // If on the first page of the onboarding
            if (viewPager?.currentItem == 0) {
                clearOnboardingPreferences() // Clear onboarding preferences
                finish() // Exit the app
            } else {
                // If no more pages, use default back behavior
                super.onBackPressed()
            }
        }
    }


}
