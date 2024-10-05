package com.example.sekeni.data.local


import android.content.Context

class PreferencesHelper(private val context: Context) {
    private val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setOnboardingFinished(isFinished: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("Finished", isFinished)
            apply()
        }
    }

    fun isOnboardingFinished(): Boolean {
        return sharedPref.getBoolean("Finished", false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("LoggedIn", isLoggedIn)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean("LoggedIn", false)
    }

    fun clearPreferences() {
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }
}
