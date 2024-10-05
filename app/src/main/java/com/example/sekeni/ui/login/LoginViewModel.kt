package com.example.sekeni.ui.login


import androidx.lifecycle.ViewModel
import com.example.sekeni.repository.LoginRepository
import com.facebook.AccessToken


class LoginViewModel : ViewModel() {

    private lateinit var loginRepository: LoginRepository

    val user
        get() = loginRepository.user

    val loading
        get() = loginRepository.loading

    fun setLoginRepository(repository: LoginRepository) {
        this.loginRepository = repository
    }

    fun signInWithGoogle(token: String) {
        loginRepository.signInWithGoogle(token)
    }

    fun signInWithFacebook(token: AccessToken) {
        loginRepository.signInWithFacebook(token)
    }

    fun checkCurrentUser() {
        loginRepository.checkCurrentUser()
    }
}
