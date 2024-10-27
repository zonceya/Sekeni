package com.example.sekeni

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sekeni.data.local.FacebookAuthManager
import com.example.sekeni.data.local.GoogleAuthManager
import com.example.sekeni.data.local.PreferencesHelper
import com.example.sekeni.databinding.ActivityMainBinding
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleAuthManager: GoogleAuthManager
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var facebookAuthManager: FacebookAuthManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // Install splash screen immediately
        super.onCreate(savedInstanceState)

        val app = application as SekeniApplication
        facebookAuthManager = app.facebookAuthManager
        googleAuthManager = app.googleAuthManager
        preferencesHelper = PreferencesHelper(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inflate binding only once
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup NavController only once
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        preferencesHelper.clearPreferences() // Clear preferences when app starts
        setSupportActionBar(binding.appBarMain.toolbar)

        // Setup Navigation Drawer and ActionBar with NavController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_store, R.id.nav_wallet, R.id.nav_purchases, R.id.nav_settings
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Set NavigationView listener
        binding.navView.setNavigationItemSelectedListener { item ->
            onNavigationItemSelected(item)
        }

        // Check login and onboarding status
        checkOnboardingAndLoginStatus()
    }

    private fun checkOnboardingAndLoginStatus() {
        when {
            !preferencesHelper.isOnboardingFinished() -> {
                navController.navigate(R.id.viewPagerFragment) // Navigate to onboarding
            }
            !preferencesHelper.isLoggedIn() -> {
                navController.navigate(R.id.loginFragment) // Navigate to login
            }
            else -> {
                navController.navigate(R.id.nav_home) // Navigate to home if logged in
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu) // Inflate action bar menu
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> navController.navigate(R.id.nav_userProfile)
            R.id.nav_store -> navController.navigate(R.id.nav_store)
            R.id.nav_wallet -> navController.navigate(R.id.nav_wallet)
            R.id.nav_purchases -> navController.navigate(R.id.nav_purchases)
            R.id.nav_settings -> navController.navigate(R.id.nav_settings)
            R.id.nav_logout -> performLogout() // Handle logout
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun performLogout() {
        auth.signOut()
        LoginManager.getInstance().logOut()
        googleAuthManager.googleSignInClient.signOut().addOnCompleteListener(this) {
            preferencesHelper.clearPreferences()
            navController.navigate(R.id.loginFragment) // Navigate to login on logout
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
