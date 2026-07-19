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
        notificationLeadTime = settings.notificationLeadTime,
        onToggleDarkMode = { viewModel.toggleDarkMode() },
        onNotificationLeadTimeChange = { viewModel.setNotificationLeadTime(it) }
    )
}

// Nur UI
@Composable
fun SettingsScreenContent(
    isDarkModeActive: Boolean,
    notificationLeadTime: Int,
    onToggleDarkMode: () -> Unit, // Eine Funktion, die aufgerufen wird, wenn man klickt
    onNotificationLeadTimeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var leadTimeText by remember(notificationLeadTime) { mutableStateOf(notificationLeadTime.toString()) }

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
                    text = "Dark Mode",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (isDarkModeActive) "Dark design is active" else "Light design is active",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isDarkModeActive,
                onCheckedChange = { onToggleDarkMode() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Notification Lead Time",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Minutes before the timer expires",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = leadTimeText,
                onValueChange = {
                    leadTimeText = it
                    it.toIntOrNull()?.let { newValue ->
                        onNotificationLeadTimeChange(newValue)
                    }
                },
                modifier = Modifier.width(80.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
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
            notificationLeadTime = 10,
            onToggleDarkMode = {},
            onNotificationLeadTimeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Settings - Dark Mode")
@Composable
fun SettingsScreenPreviewDark() {
    ParkQuickTheme {
        SettingsScreenContent(
            isDarkModeActive = true,
            notificationLeadTime = 15,
            onToggleDarkMode = {},
            onNotificationLeadTimeChange = {}
        )
    }
}