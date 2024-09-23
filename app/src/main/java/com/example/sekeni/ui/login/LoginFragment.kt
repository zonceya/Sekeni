package com.example.sekeni.ui.login

import android.content.Context
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
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.sekeni.R
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.bumptech.glide.request.RequestOptions
import com.example.sekeni.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.shobhitpuri.custombuttons.GoogleSignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONException

class LoginFragment : Fragment() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var facebookLoginButton: LoginButton
    private lateinit var videoView: VideoView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInButton: LinearLayout
    private val RC_SIGN_IN = 9001
    private var isNavigating = false
    private var isSignInTaskRunning = false
    private var idToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val drawerLayout = (activity as MainActivity).findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        // Video View
        videoView = view.findViewById(R.id.videoView)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        val videoPath = "android.resource://" + requireActivity().packageName + "/" + R.raw.background_video
        videoView.setVideoURI(Uri.parse(videoPath))
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        auth = FirebaseAuth.getInstance()

        googleSignInButton = view.findViewById<LinearLayout>(R.id.sign_in_button)


        googleSignInButton.setOnClickListener {
            signInGoogle()
        }
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        auth = Firebase.auth
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
       profileImage = headerView.findViewById<ImageView>(R.id.userProfileImage)
       profileName = headerView.findViewById<TextView>(R.id.profileName)


        facebookLoginButton = view.findViewById(R.id.login_button)
        facebookLoginButton.setPermissions("public_profile", "email")

        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
                loadingIndicator.visibility = View.VISIBLE
            }

            override fun onCancel() {
                Toast.makeText(context, "Login Cancelled.", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Log.e("LoginFragment", "Facebook Login Error: ${error.message}")
                Toast.makeText(context, "Login Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView.start()
        }
        facebookLoginButton.visibility = View.VISIBLE
        googleSignInButton.visibility = View.VISIBLE
        checkLoginState()
        return view
    }
    private fun checkLoginState() {
        // Check Google Sign-In status
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (googleAccount != null) {
            // User is signed in with Google
            loadingIndicator.visibility = View.VISIBLE
            facebookLoginButton.visibility = View.GONE
            handleGoogleSignIn(googleAccount)
        } else {
            // Check Facebook login status
            val accessToken = AccessToken.getCurrentAccessToken()
            if (accessToken != null && !accessToken.isExpired) {
                // User is already logged in with Facebook
                loadingIndicator.visibility = View.VISIBLE
                facebookLoginButton.visibility = View.GONE
                googleSignInButton.visibility = View.GONE
                handleFacebookAccessToken(accessToken)
            } else {
                // User is not logged in with either Google or Facebook
                loadingIndicator.visibility = View.GONE
                facebookLoginButton.visibility = View.VISIBLE
                googleSignInButton.visibility = View.VISIBLE
            }
        }
    }
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleGoogleSignIn(account: GoogleSignInAccount) {
        val idToken = account.idToken
        if (idToken != null) {
            firebaseAuthWithGoogle(idToken)
        } else {
            Log.e("LoginFragment", "Google Sign-In Failed: ID Token is null")
            Toast.makeText(context, "Google Sign-In Failed.", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("ID_TOKEN", idToken)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            idToken = savedInstanceState.getString("ID_TOKEN")
        }
    }


    private fun checkAndRefreshToken() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result?.token
                    // Use the refreshed token
                    Log.d("LoginFragment", "Token refreshed: $idToken")
                    // You can navigate to the home screen or update the UI here
                    updateUI(currentUser, currentUser.displayName, currentUser.photoUrl.toString())
                } else {
                    // Token refresh failed, re-authenticate the user
                    Log.e("LoginFragment", "Token refresh failed: ${task.exception?.message}")
                    promptReauthentication()
                }
            }
        } else {
            // No user is signed in, show the login UI
            navigateToLogin()
        }
    }

    private fun promptReauthentication() {
        Toast.makeText(context, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
        // Show the login UI
        navigateToLogin()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        facebookLoginButton.visibility = View.GONE
        googleSignInButton.visibility = View.GONE
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    faceBookFetchUserProfile(token)
                } else {
                    Log.e("LoginFragment", "Authentication Failed: ${task.exception?.message}")
                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
                loadingIndicator.visibility = View.GONE
            }
    }
    private fun refreshUserToken(user: FirebaseUser) {
        user.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newToken = task.result?.token
                Log.d("LoginFragment", "Token refreshed: $newToken")
                updateUI(user, user.displayName, user.photoUrl.toString())
            } else {
                Log.e("LoginFragment", "Token refresh failed: ${task.exception?.message}")
                promptReauthentication()
            }
        }
    }
    private fun faceBookFetchUserProfile(token: AccessToken) {
        val request = GraphRequest.newMeRequest(token) { obj, _ ->
            try {
                val name = obj?.getString("name")
                val profilePicUrl = obj?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")

                updateUI(auth.currentUser, name, profilePicUrl)
                Handler().postDelayed({
                    navigateToHome()
                }, 3000) // 2000 milliseconds = 4 seconds

            } catch (e: JSONException) {
                Log.e("LoginFragment", "JSON Exception: ${e.message}")
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "name,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun updateUI(user: FirebaseUser?, name: String?, profilePicUrl: String?) {
        user?.let {

            if (name.isNullOrEmpty() || profilePicUrl.isNullOrEmpty()) {
                // Navigate to the login page if the user is null or name/profilePicUrl is empty
                navigateToLogin()
                return
            }
            // Show loading indicator
            loadingIndicator.visibility = View.VISIBLE

            profileName.text = name
            profileName.visibility = View.VISIBLE

            val requestOptions = RequestOptions()
                .override(50, 50)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)

            Glide.with(requireContext())
                .load(profilePicUrl)
                .apply(requestOptions)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_launcher_foreground)  // Replace with your placeholder image
                .error(R.drawable.rectangular)  // Replace with your error image
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        e?.logRootCauses("Glide")
                        Log.e("Glide", "Error loading image", e)
                        // Hide loading indicator
                        loadingIndicator.visibility = View.GONE
                        navigateToLogin()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Hide loading indicator
                        loadingIndicator.visibility = View.GONE
                        navigateToHome()
                        return false
                    }
                })
                .into(profileImage)


            profileImage.visibility = View.VISIBLE
            facebookLoginButton.visibility = View.GONE
            googleSignInButton.visibility = View.GONE
        }
    }


    private fun navigateToHome() {
        // Save login status to shared preferences
        val sharedPref = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("LoggedIn", true)
            apply()
        }
        loadingIndicator.visibility = View.GONE
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu
        menu.findItem(R.id.nav_logout).isVisible = true

        // Use NavOptions to handle navigation properly
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)  // Ensure the back stack is cleared
            .build()
        // Navigate to the Home fragment
        findNavController().navigate(R.id.action_loginFragment_to_nav_home)
    }
    private fun navigateToLogin() {
        // Reset isNavigating flag to prevent redundant navigation
        isNavigating = false
        Log.d("LoginFragment", "Navigating to Login Fragment")

        // Use a post-delayed runnable to ensure UI updates happen after navigation
        Handler(Looper.getMainLooper()).post {
            // Navigate to Login Fragment
            findNavController().navigate(R.id.loginFragment)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle Facebook Login
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Handle Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Log.w("LoginFragment", "Google sign-in failed", e)
                Toast.makeText(context, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                // Show the login button and hide the loading indicator
                requireActivity().runOnUiThread {
                    // Show the login button and hide the loading indicator
                    loadingIndicator.visibility = View.VISIBLE
                    facebookLoginButton.visibility = View.VISIBLE
                    googleSignInButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        if (isSignInTaskRunning) {
            // Sign-in task is already running, do nothing
            Log.d("LoginFragment", "Sign-in task is already running")
            return
        }

        isSignInTaskRunning = true
     //   val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        refreshUserToken(user)
                    } else {
                        Log.e("LoginFragment", "FirebaseAuth currentUser is null after successful sign-in")
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w("LoginFragment", "Firebase authentication failed", task.exception)
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    loadingIndicator.visibility = View.GONE
                    facebookLoginButton.visibility = View.VISIBLE
                    googleSignInButton.visibility = View.VISIBLE
                }
            }
    }


    override fun onPause() {
        super.onPause()
        videoView.pause() // Pause video when leaving the fragment
    }
    override fun onResume() {
        super.onResume()
        if (!isNavigating) {
            isNavigating = true
            videoView.start()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {

        }
      //  checkAndRefreshToken()
    }
       // checkAndRefreshToken() // Check and refresh token on resume

    override fun onDestroyView() {
        super.onDestroyView()

        // Unlock the drawer when leaving LoginFragment
        val drawerLayout = (activity as MainActivity).findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}
