package com.example.sekeni.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sekeni.data.local.FacebookAuthManager
import com.example.sekeni.repository.LoginRepository
import com.facebook.AccessToken
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
    fun signInWithFacebook(token: AccessToken) {
        facebookAuthManager.handleFacebookAccessToken(token) { user ->
            _facebookUser.postValue(user)
        }
    }

    // Fetch user profile after successful sign-in
    fun fetchFacebookUserProfile(token: AccessToken) {
        facebookAuthManager.fetchUserProfile(token) { name, profilePicUrl ->
            _profileInfo.postValue(Pair(name, profilePicUrl))
        }
    }

    fun checkCurrentUser() {
        loginRepository.checkCurrentUser()
    }

}
