package com.example.sekeni.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sekeni.data.local.FacebookAuthManager
import com.facebook.AccessToken

class HomeViewModel
    : ViewModel() {
    var userName: String? = null
    var userProfilePicUrl: String? = null

    fun updateUserProfile(name: String, profilePicUrl: String) {
        userName = name
        userProfilePicUrl = profilePicUrl
    }
}
