package com.example.sekeni.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sekeni.data.local.FacebookAuthManager
import com.example.sekeni.repository.LoginRepository
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {

    private lateinit var loginRepository: LoginRepository
    private lateinit var facebookAuthManager: FacebookAuthManager

    // Expose user LiveData
    private val _facebookUser = MutableLiveData<FirebaseUser?>()
    val facebookUser: LiveData<FirebaseUser?> get() = _facebookUser

    // Expose profile info LiveData
    private val _profileInfo = MutableLiveData<Pair<String?, String?>>()
    val profileInfo: LiveData<Pair<String?, String?>> get() = _profileInfo

    // Expose loading state from repository
    val user
        get() = loginRepository.user

    val loading
        get() = loginRepository.loading

    // Initialize the repository and FacebookAuthManager
    fun setLoginRepository(repository: LoginRepository) {
        this.loginRepository = repository
    }

    fun setFacebookAuthManager(manager: FacebookAuthManager) {
        this.facebookAuthManager = manager
    }
    fun signInWithGoogle(token: String) {
        loginRepository.signInWithGoogle(token)
    }
    // Use FacebookAuthManager to handle Facebook login and credentials
    fun signInWithFacebook(token: String, callback: (FirebaseUser?) -> Unit) {
        // Get the Facebook AccessToken
        val credential = FacebookAuthProvider.getCredential(token)

        // Call the repository's sign-in method
        loginRepository.signInWithFacebook(credential) { firebaseUser ->
            if (firebaseUser != null) {
                // Sign-in successful, check current user
                checkCurrentUser()
                callback(firebaseUser)  // Return the Firebase user
            } else {
                // Handle failure
                callback(null)
            }
        }
    }
    // Fetch user profile after successful sign-in
    fun fetchFacebookUserProfile(token: AccessToken, callback: (String, String) -> Unit) {
        // Use FacebookAuthManager to fetch user profile
        facebookAuthManager.fetchUserProfile(token) { name, profilePicUrl ->
            if (profilePicUrl != null) {
                if (name != null) {
                    callback(name, profilePicUrl)
                }
            }  // Pass both values to the callback
        }
    }

    fun checkCurrentUser() {
        loginRepository.checkCurrentUser()
    }

}
