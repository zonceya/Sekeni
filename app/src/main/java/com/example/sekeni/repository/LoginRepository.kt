package com.example.sekeni.repository


import androidx.lifecycle.MutableLiveData
import com.example.sekeni.data.local.FacebookAuthManager
import com.example.sekeni.data.local.GoogleAuthManager
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginRepository(
    private val googleAuthManager: GoogleAuthManager,
    private val facebookAuthManager: FacebookAuthManager
) {

    val user = MutableLiveData<FirebaseUser?>()
    val loading = MutableLiveData<Boolean>()

    fun signInWithGoogle(token: String) {
        loading.value = true
        googleAuthManager.signIn(token) { firebaseUser ->
            user.value = firebaseUser
            loading.value = false
        }
    }

    fun signInWithFacebook(credential: AuthCredential, callback: (FirebaseUser?) -> Unit) {
        loading.value = true
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            loading.value = false
            if (task.isSuccessful) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                user.value = firebaseUser
                callback(firebaseUser)
            } else {
                callback(null)
            }
        }
    }

    fun checkCurrentUser() {
        user.value = FirebaseAuth.getInstance().currentUser
    }
}
