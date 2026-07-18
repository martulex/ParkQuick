package com.team12.parkquick.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class UserSettingsRepository (private val context : Context){

    val DARK_MODE = booleanPreferencesKey("theme_dark_mode")
    val NOTIFICATION_LEAD_TIME = intPreferencesKey("notification_lead_time")

    suspend fun toggleAppDarkMode () {
        context.dataStore.edit { settings ->
            val currentMode = settings[DARK_MODE] ?: false
            settings[DARK_MODE] = !currentMode
        }
    }

    suspend fun setNotificationLeadTime(minutes: Int) {
        context.dataStore.edit { settings ->
            settings[NOTIFICATION_LEAD_TIME] = minutes
        }
    }

    fun getUserSettings(): Flow<UserSettings> {
        return context.dataStore.data.map { settings ->
            // 1. Wert aus dem Speicher lesen
            val isDark = settings[DARK_MODE] ?: false
            val leadTime = settings[NOTIFICATION_LEAD_TIME] ?: 10

            // 2. Wert in Model (die Data Class) packen und zurückgeben
            UserSettings(isDarkModeEnabled = isDark, notificationLeadTime = leadTime)
        }
    }

}