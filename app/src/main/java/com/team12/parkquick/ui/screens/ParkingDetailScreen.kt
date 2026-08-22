package com.team12.parkquick.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.ui.components.StaticMapPreview
import com.team12.parkquick.ui.theme.ParkQuickTheme
import com.team12.parkquick.utilities.LocationUtils
import com.team12.parkquick.viewmodels.ParkingViewModel
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Composable
fun ParkingDetailScreen(
    parkingId: String,
    viewModel: ParkingViewModel,
    onNavigateBack: () -> Unit
) {
    var parkingObj by remember { mutableStateOf<ParkingCard?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(parkingId) {
        parkingObj = viewModel.getParkingByID(parkingId)
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        ParkingDetailContent(
            parkingObj = parkingObj,
            onDeleteClick = {
                parkingObj?.let {
                    viewModel.deleteParking(it)
                    onNavigateBack()
                }
            },
            onFinishParkingClick = {
                parkingObj?.let {
                    viewModel.endParking(it)
                    onNavigateBack()
                }
            },
            onStartParkingClick = { minutes ->
                parkingObj?.let {
                    // Startet den Parkplatz neu mit den bestehenden Daten
                    viewModel.addNewParking(
                        minutes = minutes,
                        name = it.name,
                        notes = it.description,
                        lat = it.latitude,
                        lng = it.longitude,
                        image = it.image,
                        price = it.price,
                        spots = it.amountOfSpots,
                        isPublic = false // Reaktivierte Spots sind erstmal lokal
                    )
                    onNavigateBack()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingDetailContent(
    parkingObj: ParkingCard?,
    onDeleteClick: () -> Unit = {},
    onFinishParkingClick: () -> Unit = {},
    onStartParkingClick: (Long) -> Unit = {}
) {
    if (parkingObj == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Parking Spot not found")
        }
        return
    }

    val context = LocalContext.current
    val sdf = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.getDefault())

    var showStartDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf(60L) }
    var isCustomTimeSelected by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = parkingObj.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Price", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${parkingObj.price} € / h", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Spots", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${parkingObj.amountOfSpots}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Opening Time", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${parkingObj.openTime} - ${parkingObj.closeTime}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
            }

            if (parkingObj.image.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(parkingObj.image),
                    contentDescription = "Parking photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            HorizontalDivider(color = Color.LightGray)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = parkingObj.description.ifEmpty { "No description provided." })
            }

            HorizontalDivider(color = Color.LightGray)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Location:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                StaticMapPreview(
                    lat = parkingObj.latitude,
                    lng = parkingObj.longitude,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            HorizontalDivider(color = Color.LightGray)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (parkingObj.isInParking) {
                Button(
                    onClick = onFinishParkingClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Finish Parking")
                }
            } else {
                Button(
                    onClick = { showStartDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Start Parking")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        LocationUtils.openNavigation(context, parkingObj.latitude, parkingObj.longitude)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Route")
                }

                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }
        }
    }


    if (showStartDialog) {
        AlertDialog(
            onDismissRequest = { showStartDialog = false },
            title = { Text("Start Parking") },
            text = {
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        onStartParkingClick(selectedMinutes)
                        showStartDialog = false
                    }
                ) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ParkingDetailPreview() {
    val dummyParking = ParkingCard(
        id = "preview_123",
        name = "Test-Parkplatz 565",
        price = 2.50f,
        description = "Ein super Parkplatz direkt im Zentrum.",
        image = "https://images.unsplash.com/photo-1590674899484-d5640e854abe?q=80&w=2067&auto=format&fit=crop",
        amountOfSpots = 1,
        openTime = "08:00",
        closeTime = "18:00",
        parkingTimeStart = System.currentTimeMillis() - 3600000,
        parkingTimeEnd = System.currentTimeMillis() + 3600000,
        isInParking = false,
        isInDiscover = false,
        latitude = 50.9413,
        longitude = 6.9583
    )

    ParkQuickTheme {
        ParkingDetailContent(
            parkingObj = dummyParking,
            onDeleteClick = {},
            onFinishParkingClick = {},
            onStartParkingClick = {}
        )
    }
}