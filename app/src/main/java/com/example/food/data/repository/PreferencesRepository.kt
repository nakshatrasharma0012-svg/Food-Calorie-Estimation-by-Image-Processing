package com.example.food.data.repository

import android.content.Context
import android.content.SharedPreferences

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("food_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_CUSTOM_BACKEND_URL = "custom_backend_url"
        private const val KEY_API_MODE = "api_mode" // "DIRECT_GEMINI" or "CUSTOM_BACKEND"
        private const val KEY_CALORIE_GOAL = "calorie_goal"
        private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    var geminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_API_KEY, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_GEMINI_API_KEY, value.trim()).apply()
        }

    var customBackendUrl: String
        get() = prefs.getString(KEY_CUSTOM_BACKEND_URL, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_CUSTOM_BACKEND_URL, value.trim()).apply()
        }

    var apiMode: String
        get() = prefs.getString(KEY_API_MODE, "DIRECT_GEMINI") ?: "DIRECT_GEMINI"
        set(value) {
            prefs.edit().putString(KEY_API_MODE, value).apply()
        }

    var dailyCalorieGoal: Int
        get() = prefs.getInt(KEY_CALORIE_GOAL, 2000)
        set(value) {
            prefs.edit().putInt(KEY_CALORIE_GOAL, value).apply()
        }

    var loggedInUserEmail: String
        get() = prefs.getString(KEY_LOGGED_IN_EMAIL, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_LOGGED_IN_EMAIL, value).apply()
        }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) {
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()
        }

    fun logout() {
        isLoggedIn = false
        loggedInUserEmail = ""
    }
}
