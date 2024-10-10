package com.example.sekeni.data.local

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONException

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
    fun fetchUserProfile(token: AccessToken, onComplete: (String?, String?) -> Unit) {
        val request = GraphRequest.newMeRequest(token) { obj, _ ->
            try {
                val name = obj?.getString("name")
                val profilePicUrl = obj?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")
                onComplete(name, profilePicUrl)
            } catch (e: JSONException) {
                Log.e("FacebookAuthManager", "JSON Exception: ${e.message}")
                onComplete(null, null)
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "name,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()
    }

}
