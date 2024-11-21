package com.example.sekeni

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.legacy.app.ActionBarDrawerToggle
import androidx.navigation.NavController
import androidx.navigation.findNavController
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleAuthManager: GoogleAuthManager
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var facebookAuthManager: FacebookAuthManager
    private lateinit var navController: NavController
    private var lastBackPressedTime: Long = 0
    private val doubleBackPressDuration = 2000L
    private lateinit var fab: FloatingActionButton
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
        setSupportActionBar(binding.appBarMain.toolbar)

        val headerView = binding.navView.getHeaderView(0) // Assuming `navView` is your NavigationView
        val apiVersionText = headerView.findViewById<TextView>(R.id.api_version)

        preferencesHelper.clearPreferences() // Clear preferences when app starts

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        // Setup Navigation Drawer and ActionBar with NavController
        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_profile,
                R.id.nav_store,
                R.id.nav_wallet,
                R.id.nav_purchases,
                R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fab = findViewById<FloatingActionButton>(R.id.fab)
            supportActionBar?.let { actionBar ->
                if (destination.id == R.id.loginFragment) {
                    fab.visibility = View.GONE  // Hide the FAB
                    Handler(Looper.getMainLooper()).postDelayed({
                        actionBar?.hide()
                    }, 200)  // Hide the action bar
                } else {
                    fab.visibility = View.VISIBLE  // Show the FAB for other fragments
                    actionBar.show()  // Show the action bar for other fragments
                }
            }
        }
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
                navController.navigate(R.id.nav_home)
                supportActionBar?.show()// Navigate to home if logged in
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        Log.d("MainActivity", "Options menu created")
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
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
            val currentDestinationId = navController.currentDestination?.id

            if (preferencesHelper.isLoggedIn() && currentDestinationId == R.id.loginFragment) {
                // Close the app instead of navigating back to login if user is logged in
                finish()
            } else if (currentDestinationId == R.id.loginFragment) {
                // Double back press to exit on login screen
                if (System.currentTimeMillis() - lastBackPressedTime < doubleBackPressDuration) {
                    finish() // Close the app on double-back press
                } else {
                    lastBackPressedTime = System.currentTimeMillis()
                    // Prompt the user
                    Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            } else {
                super.onBackPressed()
            }
        }
    }
}

