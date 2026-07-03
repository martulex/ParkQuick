package com.team12.parkquick.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.team12.parkquick.viewmodels.ParkingViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParkingScreen(onNavigateBack : () -> Unit, viewModel: ParkingViewModel) {

    val context = LocalContext.current
    var notes by remember { mutableStateOf("") }
    var selectedMinutes by remember { mutableStateOf(60L) }
    var name by remember { mutableStateOf("") }
    var isCustomSelected by remember { mutableStateOf(false) }

    fun showDateTimePicker() {
        val currentCalendar = Calendar.getInstance()

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val targetDateTime = LocalDateTime.of(
                            LocalDate.of(year, month + 1, dayOfMonth),
                            LocalTime.of(hourOfDay, minute)
                        )
                        val diffInMinutes = Duration.between(LocalDateTime.now(), targetDateTime).toMinutes()

                        if (diffInMinutes > 0) {
                            selectedMinutes = diffInMinutes
                            isCustomSelected = true
                        }
                    },
                    currentCalendar.get(Calendar.HOUR_OF_DAY),
                    currentCalendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Location: Karte wird direkt als Vorschau angezeigt
        Text(text = "Location", style = MaterialTheme.typography.titleMedium)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Map Preview", color = Color.Gray)
            }
        }

        // Timer: Schnellauswahl für Zeiten
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Text-Anzeige formatiert große Minutenzahlen lesbar in Stunden um, falls über Custom gewählt
            val displayText = if (selectedMinutes >= 60) "${selectedMinutes / 60}h ${selectedMinutes % 60}m" else "${selectedMinutes} min"
            Text(text = "Set Timer: $displayText", style = MaterialTheme.typography.titleMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedMinutes == 30L && !isCustomSelected,
                    onClick = { selectedMinutes = 30L; isCustomSelected = false },
                    label = { Text("30m") }
                )
                FilterChip(
                    selected = selectedMinutes == 60L && !isCustomSelected,
                    onClick = { selectedMinutes = 60L; isCustomSelected = false },
                    label = { Text("1h") }
                )
                FilterChip(
                    selected = selectedMinutes == 120L && !isCustomSelected,
                    onClick = { selectedMinutes = 120L; isCustomSelected = false },
                    label = { Text("2h") }
                )
                // Der Custom-Button öffnet jetzt die nativen Picker-Dialoge
                FilterChip(
                    selected = isCustomSelected,
                    onClick = { showDateTimePicker() },
                    label = { Text("Custom") }
                )
            }
        }

        // Photo Sektion
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Photo", style = MaterialTheme.typography.titleMedium)

            OutlinedButton(
                onClick = { /* Open Camera */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Take Photo")
            }

        }

        OutlinedTextField(
            value = name,
            onValueChange = {name = it},
            label = {Text("Name (e.g. Cinema, Work)")},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Notes
        OutlinedTextField(
            value = notes, // Das Textfeld schaut in die Merkzelle notes und zeigt dem Nutzer immer genau den Text an, der dort gerade abgespeichert ist.
            onValueChange = { notes = it }, // Jedes Mal, wenn der Nutzer eine Taste drückt, fängt Android den neuen Text ab (it) und speichert ihn sofort in notes ab, damit der Bildschirm sich mit dem neuen Buchstaben aktualisieren kann.
            label = { Text("Notes (e.g. Floor, Pillar number)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        //Spacer(modifier = Modifier.height(24.dp))

        // Bestätigen
        Button(
            onClick = { viewModel.addNewParking(selectedMinutes, name, notes)
                onNavigateBack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Parking Spot")
        }
    }
}