package com.example.sekeni


import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.sekeni.data.local.FacebookAuthManager
import com.example.sekeni.data.local.GoogleAuthManager
import com.example.sekeni.data.local.PreferencesHelper
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
    private lateinit var googleAuthManager: GoogleAuthManager
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var facebookAuthManager: FacebookAuthManager
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            installSplashScreen()
        }, 4000)
        val app = application as SekeniApplication
        facebookAuthManager = app.facebookAuthManager
        googleAuthManager = app.googleAuthManager
        preferencesHelper = PreferencesHelper(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        Log.d("MainActivity", "NavController initialized: $navController")
        preferencesHelper.clearPreferences()
        setSupportActionBar(binding.appBarMain.toolbar)
        LoginManager.getInstance().logOut()

        checkOnboardingAndLoginStatus()
    }

    private fun checkOnboardingAndLoginStatus() {
        // Log current destination and preferences
        Log.d("NavigationCheck", "Current Destination: ${navController.currentDestination?.id}")
        Log.d("Preferences", "Onboarding Finished: ${preferencesHelper.isOnboardingFinished()}")
        Log.d("Preferences", "Logged In: ${preferencesHelper.isLoggedIn()}")

        when {
            !preferencesHelper.isOnboardingFinished() -> {
                // If onboarding is not finished
                Log.d("Navigation", "Navigating to ViewPagerFragment (Onboarding)")
                navController.navigate(R.id.viewPagerFragment)
            }

            !preferencesHelper.isLoggedIn() -> {
                // If not logged in, navigate to loginFragment
                Log.d("Navigation", "Navigating to loginFragment (User not logged in)")
                navController.navigate(R.id.loginFragment)
            }

            navController.currentDestination?.id != R.id.nav_home -> {
                // If logged in, navigate to home screen
                Log.d("Navigation", "Navigating to HomeFragment (User logged in)")
                navController.navigate(R.id.nav_home)
                setupNavigationDrawer(navController)
            }
        }
    }

    private fun clearOnboardingPreferences() {
        preferencesHelper.clearPreferences()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer(navController: NavController) {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            performLogout()
            true
        }
    }
    private fun performLogout() {
        Log.d("MainActivity", "Signing out from Firebase")
        auth.signOut() // Sign out from Firebase

        Log.d("MainActivity", "Logging out from Facebook")
        LoginManager.getInstance().logOut() // Log out from Facebook

        Log.d("MainActivity", "Signing out from Google")
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Use PreferencesHelper to clear login status and other preferences
            preferencesHelper.clearPreferences() // Clears all preferences
        }

        Log.d("MainActivity", "Navigating to login screen")
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.loginFragment)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        val navView: NavigationView = findViewById(R.id.nav_view)

// Set the logout action for the menu item
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            performLogout()
            true
        }

    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            viewPager != null && viewPager.currentItem > 0 -> viewPager.currentItem -= 1
            viewPager?.currentItem == 0 -> {
                clearOnboardingPreferences()
                finish()
            }
            else -> super.onBackPressed()
        }
    }

}
