package com.example.sekeni

import android.app.Application
import com.example.sekeni.data.local.FacebookAuthManager
import com.example.sekeni.data.local.GoogleAuthManager

class SekeniApplication : Application() {
    lateinit var googleAuthManager: GoogleAuthManager
    lateinit var facebookAuthManager: FacebookAuthManager

    override fun onCreate() {
        super.onCreate()
        facebookAuthManager = FacebookAuthManager().apply {
            initializeFacebook(this@SekeniApplication)
        }
        googleAuthManager = GoogleAuthManager(this)
    }
}
