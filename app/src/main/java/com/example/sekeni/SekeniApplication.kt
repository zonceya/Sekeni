package com.example.sekeni

import android.app.Application
import com.example.sekeni.data.local.FacebookAuthManager
import com.example.sekeni.data.local.GoogleAuthManager
import com.example.sekeni.repository.LoginRepository

class SekeniApplication : Application() {
    lateinit var googleAuthManager: GoogleAuthManager
    lateinit var facebookAuthManager: FacebookAuthManager
    lateinit var loginRepository: LoginRepository

    override fun onCreate() {
        super.onCreate()

        // Initialize FacebookAuthManager
        facebookAuthManager = FacebookAuthManager().apply {
            initializeFacebook(this@SekeniApplication)
        }

        // Initialize GoogleAuthManager
        googleAuthManager = GoogleAuthManager(this)

        // Initialize LoginRepository using both managers
        loginRepository = LoginRepository(googleAuthManager, facebookAuthManager)
    }
}
