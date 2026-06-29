package com.team12.parkquick.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team12.parkquick.settings.UserSettingsViewModel
import com.team12.parkquick.ui.theme.ParkQuickTheme

// (wird vom NavHost aufgerufen, kommuniziert mit viewModel)
@Composable
fun SettingsScreen( viewModel: UserSettingsViewModel) {
    // Daten aus dem ViewModel sammeln
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        modifier = Modifier,
        isDarkModeActive = settings.isDarkModeEnabled,
        onToggleDarkMode = { viewModel.toggleDarkMode() }
    )
}

// Nur UI
@Composable
fun SettingsScreenContent(
    isDarkModeActive: Boolean,
    onToggleDarkMode: () -> Unit, // Eine Funktion, die aufgerufen wird, wenn man klickt
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dunkelmodus",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (isDarkModeActive) "Dunkles Design ist aktiv" else "Helles Design ist aktiv",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isDarkModeActive,
                onCheckedChange = { onToggleDarkMode() }
            )
        }
    }
}

// Preview mit Fake Daten

@Preview(showBackground = true, name = "Settings - Light Mode")
@Composable
fun SettingsScreenPreviewLight() {
    ParkQuickTheme {
        SettingsScreenContent(
            isDarkModeActive = false,
            onToggleDarkMode = {}
        )
    }
}

@Preview(showBackground = true, name = "Settings - Dark Mode")
@Composable
fun SettingsScreenPreviewDark() {
    ParkQuickTheme {
        SettingsScreenContent(
            isDarkModeActive = true,
            onToggleDarkMode = {}
        )
    }
}