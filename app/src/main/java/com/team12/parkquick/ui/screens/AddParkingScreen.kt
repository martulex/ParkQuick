package com.team12.parkquick.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import androidx.compose.ui.tooling.preview.Preview
import com.team12.parkquick.ui.theme.ParkQuickTheme
import com.team12.parkquick.database.ParkingCard

import com.team12.parkquick.ui.components.LocationPickerMap
import com.team12.parkquick.ui.components.StaticMapPreview
import com.team12.parkquick.viewmodels.ParkingViewModel
import java.io.File
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar

enum class AddParkingStep {
    MAP, FORM
}

@Composable
fun AddParkingScreen(onNavigateBack: () -> Unit, viewModel: ParkingViewModel) {
    val existingSpots by viewModel.allAvailableParkings.collectAsStateWithLifecycle()

    AddParkingContent(
        onNavigateBack = onNavigateBack,
        existingSpots = existingSpots,
        onSaveParking = { minutes, name, notes, lat, lng, image, price, spots, isPublic ->
            viewModel.addNewParking(minutes, name, notes, lat, lng, image, price, spots, isPublic)
            onNavigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParkingContent(
    onNavigateBack: () -> Unit,
    existingSpots: List<ParkingCard>,
    onSaveParking: (Long, String, String?, Double, Double, String, Float, Int, Boolean) -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(AddParkingStep.MAP) }

    // Navigation and Selection state
    var selectedExistingSpot by remember { mutableStateOf<ParkingCard?>(null) }

    // Form States
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedMinutes by remember { mutableStateOf(60L) }
    var price by remember { mutableStateOf(0f) }
    var amountOfSpots by remember { mutableStateOf(1) }
    var isSharedWithCommunity by remember { mutableStateOf(false) }

    var isCustomTimeSelected by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // GPS States
    var latitude by remember { mutableStateOf(50.9375) } // Default Köln
    var longitude by remember { mutableStateOf(6.9603) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri = tempPhotoUri
        }
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                      permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        if (granted) {
            // Permission granted
        }
    }

    LaunchedEffect(Unit) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED && coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude
                    }
                }
        }
    }

    if (currentStep == AddParkingStep.MAP) {
        Box(modifier = Modifier.fillMaxSize()) {
            LocationPickerMap(
                initialPosition = LatLng(latitude, longitude),
                existingSpots = existingSpots,
                onLocationPicked = {
                    latitude = it.latitude
                    longitude = it.longitude
                    selectedExistingSpot = null // Clear selection if user moves pin to new location
                },
                onSpotSelected = { spot ->
                    selectedExistingSpot = spot
                    latitude = spot.latitude
                    longitude = spot.longitude
                },
                modifier = Modifier.fillMaxSize()
            )
            Button(
                onClick = {
                    selectedExistingSpot?.let { spot ->
                        // Pre-fill logic only if spot selected
                        name = spot.name
                        description = spot.description
                        price = spot.price
                        amountOfSpots = spot.amountOfSpots
                        latitude = spot.latitude
                        longitude = spot.longitude
                        isSharedWithCommunity = spot.isSharedWithCommunity
                    }
                    currentStep = AddParkingStep.FORM
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text(if (selectedExistingSpot != null) "Continue with this parking spot" else "Confirm Location & Continue")
            }
        }
    } else {
        val isFromExisting = selectedExistingSpot != null

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = "Parking Details", style = MaterialTheme.typography.headlineSmall)

            // Preview Map
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
                OutlinedButton(
                    onClick = { currentStep = AddParkingStep.MAP },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Change")
                }
            }

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name (e.g. Cinema, Work)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = isFromExisting
            )

            // Price & Spots
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = if (price == 0f) "" else price.toString(),
                    onValueChange = { price = it.toFloatOrNull() ?: 0f },
                    label = { Text("Price (€/h)") },
                    modifier = Modifier.weight(1f),
                    readOnly = isFromExisting
                )
                OutlinedTextField(
                    value = amountOfSpots.toString(),
                    onValueChange = { amountOfSpots = it.toIntOrNull() ?: 1 },
                    label = { Text("Spots") },
                    modifier = Modifier.weight(1f),
                    readOnly = isFromExisting
                )
            }

            // Public Toggle (Only show if NOT from existing community spot)
            if (!isFromExisting) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Share with Community", style = MaterialTheme.typography.bodyLarge)
                        Text("Make this spot visible to others", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = isSharedWithCommunity,
                        onCheckedChange = { isSharedWithCommunity = it }
                    )
                }
            }

            // Timer
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val displayText = if (selectedMinutes >= 60) "${selectedMinutes / 60}h ${selectedMinutes % 60}m" else "${selectedMinutes} min"
                Text(text = "Set Timer: $displayText", style = MaterialTheme.typography.titleMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedMinutes == 30L && !isCustomTimeSelected,
                        onClick = { selectedMinutes = 30L; isCustomTimeSelected = false },
                        label = { Text("30m") }
                    )
                    FilterChip(
                        selected = selectedMinutes == 60L && !isCustomTimeSelected,
                        onClick = { selectedMinutes = 60L; isCustomTimeSelected = false },
                        label = { Text("1h") }
                    )
                    FilterChip(
                        selected = isCustomTimeSelected,
                        onClick = {
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(context, { _, y, m, d ->
                                TimePickerDialog(context, { _, h, min ->
                                    val target = LocalDateTime.of(LocalDate.of(y, m + 1, d), LocalTime.of(h, min))
                                    val diff = Duration.between(LocalDateTime.now(), target).toMinutes()
                                    if (diff > 0) {
                                        selectedMinutes = diff
                                        isCustomTimeSelected = true
                                    }
                                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                        },
                        label = { Text("Custom") }
                    )
                }
            }

        // Photo Sektion updated
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Photo", style = MaterialTheme.typography.titleMedium)

            photoUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Parking photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            if (photoUri == null) {

                OutlinedButton(
                    onClick = {
                        val uri = createImageUri(context)
                        tempPhotoUri = uri
                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Take Photo")
                }

            } else {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {
                            val uri = createImageUri(context)
                            tempPhotoUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retake")
                    }

                    OutlinedButton(
                        onClick = {
                            photoUri = null
                            tempPhotoUri = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Remove")
                    }
                }
            }
        }

            // Notes
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description/Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Save
            Button(
                onClick = {
                    // If it's from existing community, we save it ONLY to Room (isPublic=false in addNewParking call)
                    val finalIsPublic = if (isFromExisting) false else isSharedWithCommunity
                    onSaveParking(selectedMinutes, name, description, latitude, longitude, photoUri?.toString() ?: "", price, amountOfSpots, finalIsPublic)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                enabled = name.isNotBlank()
            ) {
                Text("Save Parking Spot")
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val imageFile = File(
        context.filesDir,
        "parking_photo_${System.currentTimeMillis()}.jpg"
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

@Preview(showBackground = true)
@Composable
fun AddParkingContentPreview() {
    ParkQuickTheme {
        Surface {
            AddParkingContent(
                onNavigateBack = {},
                existingSpots = listOf(
                    ParkingCard(
                        id = "1",
                        name = "Sample Spot",
                        latitude = 50.9375,
                        longitude = 6.9603,
                        price = 1.5f,
                        amountOfSpots = 10,
                        isSharedWithCommunity = true
                    )
                ),
                onSaveParking = { _, _, _, _, _, _, _, _, _ -> }
            )
        }
    }
}
