package com.example.todoappshmr.repository

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFERENCES_NAME = "todo_preferences"
    private const val REVISION_KEY = "revision"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveRevision(context: Context, revision: Int) {
        val preferences = getPreferences(context)
        preferences.edit().putInt(REVISION_KEY, revision).apply()
    }

    fun getRevision(context: Context): Int {
        val preferences = getPreferences(context)
        return preferences.getInt(REVISION_KEY, 0)
    }
}
