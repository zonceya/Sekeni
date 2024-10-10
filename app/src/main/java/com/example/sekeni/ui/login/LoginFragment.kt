package com.example.sekeni.ui.login

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.sekeni.MainActivity
import com.example.sekeni.R
import com.example.sekeni.SekeniApplication
import com.example.sekeni.data.local.FacebookAuthManager
import com.example.sekeni.data.local.GoogleAuthManager
import com.example.sekeni.data.local.PreferencesHelper
import com.example.sekeni.repository.LoginRepository
import com.example.sekeni.ui.home.HomeFragment
import com.example.sekeni.ui.home.HomeViewModel
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var callbackManager: CallbackManager
    private lateinit var facebookLoginButton: LoginButton
    private lateinit var videoView: VideoView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInButton: LinearLayout
    private val RC_SIGN_IN = 9001
    private var isNavigating = false
    private lateinit var preferencesHelper: PreferencesHelper
    private var isSignInTaskRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        // Create an instance of the LoginRepository
        // Step 1: Initialize ViewModel
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        preferencesHelper = PreferencesHelper(requireContext())
        // Step 2: Create instances of GoogleAuthManager and FacebookAuthManager
        val googleAuthManager = GoogleAuthManager(requireContext())
        val facebookAuthManager = FacebookAuthManager()

        // Step 3: Create LoginRepository with the auth managers
        val loginRepository = LoginRepository(googleAuthManager, facebookAuthManager)

        // Step 4: Set the LoginRepository in the ViewModel
        viewModel.setLoginRepository(loginRepository)
        setupUI(view)
       // setupObservers()
        viewModel.checkCurrentUser()
        return view
    }

    private fun setupUI(view: View) {
        hideActionBar()
        lockDrawer()
        initializeVideoView(view)
        initializeGoogleSignIn(view)
        initializeFacebookLogin(view)

    }

    private fun hideActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    private fun lockDrawer() {
        val drawerLayout = (activity as MainActivity).findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun initializeVideoView(view: View) {
        videoView = view.findViewById(R.id.videoView)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        val videoPath = "android.resource://${requireActivity().packageName}/${R.raw.background_video}"
        videoView.setVideoURI(Uri.parse(videoPath))

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView.start()
        }
    }

    private fun initializeGoogleSignIn(view: View) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInButton = view.findViewById(R.id.sign_in_button)
        googleSignInButton.setOnClickListener {
            signInGoogle()
        }
    }

    private fun initializeFacebookLogin(view: View) {
        // Initialize the FacebookAuthManager and LoginRepository manually
        val facebookAuthManager = (requireActivity().application as SekeniApplication).facebookAuthManager
        val loginRepository = (requireActivity().application as SekeniApplication).loginRepository

        val viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        viewModel.setLoginRepository(loginRepository)
        viewModel.setFacebookAuthManager(facebookAuthManager)

        callbackManager = facebookAuthManager.getCallbackManager()
        facebookLoginButton = view.findViewById(R.id.login_button)
        facebookLoginButton.setPermissions("public_profile", "email")

        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // Show loading indicator
                loadingIndicator.visibility = View.VISIBLE
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                val accessToken = result.accessToken
                viewModel.signInWithFacebook(result.accessToken.token) { firebaseUser ->
                    if (firebaseUser != null) {
                        // User is successfully authenticated
                        viewModel.fetchFacebookUserProfile(accessToken) { name, profilePicUrl ->
                            // Update HomeViewModel with fetched data
                            val homeViewModel = ViewModelProvider(requireActivity()).get(
                                HomeViewModel::class.java)
                            homeViewModel.updateUserProfile(name, profilePicUrl)

                            // Optionally add a delay before navigating to HomeFragment
                            Handler().postDelayed({
                                findNavController().navigate(R.id.homeFragment)
                            }, 3000)
                        }

                    }else {
                        // Handle authentication failure
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                    // Hide loading indicator
                    loadingIndicator.visibility = View.GONE
                }
                // Create a credential using the Facebook access token

                // Sign in with Firebase using the credential

            }

            override fun onCancel() {
                Toast.makeText(context, "Login Cancelled.", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Log.e("LoginFragment", "Facebook Login Error: ${error.message}")
                Toast.makeText(context, "Error during Facebook Login: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                navigateToHome()
            } ?: run {
                loadingIndicator.visibility = View.GONE
                facebookLoginButton.show()
                googleSignInButton.show()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val facebookAuthManager = (requireActivity().application as SekeniApplication).facebookAuthManager
        facebookAuthManager.getCallbackManager().onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let {
                    viewModel.signInWithGoogle(it)
                    handleSignInResult(task)
                }
            } catch (e: ApiException) {
                Log.w("LoginFragment", "Google sign-in failed", e)
            }
        }
    }
    private fun View.show() {
        this.visibility = View.VISIBLE
    }

    private fun View.hide() {
        this.visibility = View.GONE
    }

    private fun showLoadingIndicator() {
        loadingIndicator.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
    }

    private fun navigateToLogin() {
        isNavigating = false
        Log.d("LoginFragment", "Navigating to Login Fragment")

        Handler(Looper.getMainLooper()).post {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun hideSignInButtons() {
        facebookLoginButton.visibility = View.GONE
        googleSignInButton.visibility = View.GONE
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let {
                viewModel.signInWithGoogle(it)
                //updateUI(null, account.displayName, account.photoUrl.toString())
            }
        } catch (e: ApiException) {
            Log.w("LoginFragment", "Google sign-in failed", e)
            Toast.makeText(context, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHome() {
        if (isNavigating) return
          isNavigating = true
        val options = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()
        findNavController().navigate(R.id.nav_home, null, options)
    }
}
