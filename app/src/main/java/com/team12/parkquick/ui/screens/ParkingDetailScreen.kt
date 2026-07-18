package com.team12.parkquick.ui.screens

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
            }
        )
    }
}

@Composable
fun ParkingDetailContent(
    parkingObj: ParkingCard?,
    onDeleteClick: () -> Unit = {}
) {
    if (parkingObj == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Parking Spot not found")
        }
        return
    }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {

        // --- SCROLLBARER INHALTSBEREICH ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Titel
            Text(
                text = parkingObj.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // --- INFO-BEREICH: Preis, Spots & Öffnungszeiten (Jetzt oben!) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Preis", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${parkingObj.price} € / h", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Spots", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${parkingObj.amountOfSpots}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Geöffnet", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${parkingObj.openTime} - ${parkingObj.closeTime}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
            }

            // Bild
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

            // Location Überschrift
            Text(
                text = "Location:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Map Preview
            StaticMapPreview(
                lat = parkingObj.latitude,
                lng = parkingObj.longitude,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            HorizontalDivider(color = Color.LightGray)

            // Notes
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Notes:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = parkingObj.description.ifEmpty { "No notes provided." })
            }
        }

        // --- FEST VERANKERTER BUTTON-BEREICH ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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

// --- PREVIEWS ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ParkingDetailPreview() {
    val dummyParking = ParkingCard(
        id = "preview_123",
        name = "Parkhaus am Dom",
        price = 2.50f,
        description = "Sehr zentrales Parkhaus direkt in der Innenstadt. Frauenparkplätze im Erdgeschoss.",
        image = "",
        amountOfSpots = 150,
        openTime = "06:00",
        closeTime = "23:30",
        parkingTimeStart = 0L,
        parkingTimeEnd = 0L,
        isInParking = true,
        isInDiscover = false,
        latitude = 50.9413,
        longitude = 6.9583
    )

    ParkQuickTheme {
        ParkingDetailContent(
            parkingObj = dummyParking,
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ParkingDetailPreviewEmpty() {
    ParkQuickTheme {
        ParkingDetailContent(
            parkingObj = null,
            onDeleteClick = {}
        )
    }
}