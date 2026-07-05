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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.team12.parkquick.ui.components.LocationPickerMap
import com.team12.parkquick.ui.components.StaticMapPreview
import com.team12.parkquick.viewmodels.ParkingViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.LaunchedEffect


@Composable
fun AddParkingScreen(onNavigateBack : () -> Unit, viewModel: ParkingViewModel) {
    AddParkingContent(
        onNavigateBack = onNavigateBack,
        onSaveParking = { selectedMinutes, name, notes, lat, lng ->
            viewModel.addNewParking(selectedMinutes, name, notes, lat, lng)
            onNavigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParkingContent(
    onNavigateBack: () -> Unit,
    onSaveParking: (Long, String, String?, Double, Double) -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var selectedMinutes by remember { mutableStateOf(60L) }
    var name by remember { mutableStateOf("") }
    var isCustomSelected by remember { mutableStateOf(false) }

    // GPS States
    var latitude by remember { mutableStateOf(50.9375) } // Default Köln
    var longitude by remember { mutableStateOf(6.9603) }
    var isLocationPicked by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf(false) }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                      permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        if (granted) {
            showMapPicker = true
        }
    }

    fun checkLocationPermissionAndOpenMap() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED || coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            showMapPicker = true
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    // Optional: Get current location when map is shown
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    LaunchedEffect(showMapPicker) {
        if (showMapPicker) {
            try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        location?.let {
                            latitude = it.latitude
                            longitude = it.longitude
                        }
                    }
            } catch (e: SecurityException) {
                // Should be handled by permission check above
            }
        }
    }

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

    if (showMapPicker) {
        Dialog(
            onDismissRequest = { showMapPicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    LocationPickerMap(
                        initialPosition = LatLng(latitude, longitude),
                        onLocationPicked = {
                            latitude = it.latitude
                            longitude = it.longitude
                            isLocationPicked = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { 
                            isLocationPicked = true
                            showMapPicker = false 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Confirm Location")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Location
        Text(text = "Location", style = MaterialTheme.typography.titleMedium)

        if (!isLocationPicked) {
            OutlinedButton(
                onClick = { checkLocationPermissionAndOpenMap() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Add a parking location")
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                StaticMapPreview(
                    lat = latitude,
                    lng = longitude,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Button to change location
                IconButton(
                    onClick = { checkLocationPermissionAndOpenMap() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Change Location")
                }
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
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (e.g. Location, Space ...)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Bestätigen
        val isFormValid = name.isNotBlank() && isLocationPicked
        Button(
            onClick = { onSaveParking(selectedMinutes, name, description, latitude, longitude) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Save Parking Spot")
        }
    }
}
