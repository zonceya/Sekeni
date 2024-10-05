package com.example.sekeni.data.local

import android.app.Application
import android.content.Context
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FacebookAuthManager {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    fun initializeFacebook(context: Context) {
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        // Initialize the Facebook SDK
        FacebookSdk.sdkInitialize(context)
        AppEventsLogger.activateApp(context as Application)
    }

    fun getCallbackManager(): CallbackManager {
        return callbackManager
    }

    fun signIn(token: AccessToken, onComplete: (firebaseUser: FirebaseUser?) -> Unit) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(auth.currentUser)
            } else {
                onComplete(null)
            }
        }
    }

    fun handleFacebookAccessToken(token: AccessToken, onComplete: (firebaseUser: FirebaseUser?) -> Unit) {
        signIn(token, onComplete)
    }
}
