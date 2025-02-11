package com.example.sekeni.data.local


import android.content.Context

class PreferencesHelper(private val context: Context) {
    private val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setOnboardingFinished(isFinished: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("OnboardingFinished", isFinished)
            apply()
        }
    }


    fun isOnboardingFinished(): Boolean {
        return sharedPref.getBoolean("OnboardingFinished", false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("UserLoggedIn", isLoggedIn)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean("LoggedIn", false)
    }

    fun isFirstTimeLaunch(): Boolean {
        return sharedPref.getBoolean("FirstTimeLaunch", true)
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("FirstTimeLaunch", isFirstTime)
            apply()
        }
    }

    fun clearPreferences() {
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }
}

