package com.team12.parkquick.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserSettingsViewModel (application: Application) : AndroidViewModel(application) {

    private val userSettingsRepository = UserSettingsRepository(application)

    val settingsState : StateFlow<UserSettings> = userSettingsRepository.getUserSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(false)
        )

    fun toggleDarkMode() {
        viewModelScope.launch {
            userSettingsRepository.toggleAppDarkMode()
        }
    }




}