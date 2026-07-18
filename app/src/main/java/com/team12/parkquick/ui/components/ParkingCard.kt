package com.team12.parkquick.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.ui.components.StaticMapPreview
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun ParkingCard(
    parking: ParkingCard,
    onCardClick: () -> Unit,
    onRouteClick: (() -> Unit)? = null
) {
    // Ruft unsere neue Funktion mit dem Millisekunden-Feld auf
    var timeLeftString by remember { mutableStateOf(calculateTimeLeft(parking.parkingTimeEnd)) }

    // Startet eine reaktive Coroutine, die alle 1 Sekunde die UI aktualisiert
    if (parking.isInParking) {
        // Beobachtet die parkingTimeEnd Variable
        LaunchedEffect(parking.parkingTimeEnd) {
            while (true) {
                timeLeftString = calculateTimeLeft(parking.parkingTimeEnd)
                delay(1000)
            }
        }
    }

    Card(onClick = onCardClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            Text(
                text = parking.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder Map / Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(MaterialTheme.shapes.small)
            ) {
                if (parking.image.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(parking.image),
                        contentDescription = "Parking Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    StaticMapPreview(
                        lat = parking.latitude,
                        lng = parking.longitude,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            }


            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Entweder den Live-Timer anzeigen oder einen netten Text für die Historie
                val historyText = "Finished on ${SimpleDateFormat("dd.MM.", Locale.getDefault()).format(Date(parking.parkingTimeEnd))}"

                Text(
                    text = if (parking.isInParking) "Remaining: $timeLeftString" else historyText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                if (parking.isInParking) {
                    Button(onClick = onRouteClick ?: {}) {
                        Text("Route")
                    }
                }
            }
        }
    }
}

// NEUE Version der Berechnungsfunktion, die mit Millisekunden (Long) arbeitet
fun calculateTimeLeft(pickupTimeMillis: Long): String {
    val currentTime = System.currentTimeMillis()
    val remainingMillis = pickupTimeMillis - currentTime

    if (remainingMillis <= 0) {
        return "Expired!"
    }

    // Mathematik, um Millisekunden aufzubrechen
    val totalSeconds = remainingMillis / 1000
    val days = totalSeconds / (24 * 3600)
    val hours = (totalSeconds % (24 * 3600)) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (days > 0) {
        "${days}d ${hours}h ${minutes}m"
    } else {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingCardPreviewActive() {
    val sampleParking = ParkingCard(
        name = "TH Köln Parkplatz",
        latitude = 50.0,
        longitude = 7.0,
        parkingTimeStart = System.currentTimeMillis(),
        // + 2 Stunden und 15 Minuten in die Zukunft
        parkingTimeEnd = System.currentTimeMillis() + (2 * 60 * 60 * 1000) + (15 * 60 * 1000),
        isInParking = true,
        id = UUID.randomUUID().toString(),
        price = 12f,
        description = "",
        image = "",
        amountOfSpots = 12,
        openTime = "",
        closeTime = ""
    )
    ParkingCard(
        parking = sampleParking,
        onCardClick = {},
        onRouteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ParkingCardPreviewHistory() {
    val sampleParking = ParkingCard(
        name = "TH Köln Parkplatz",
        latitude = 50.0,
        longitude = 7.0,
        parkingTimeStart = System.currentTimeMillis() - (5 * 60 * 60 * 1000), // Start vor 5 Stunden
        parkingTimeEnd = System.currentTimeMillis() - (3 * 60 * 60 * 1000),   // Ende vor 3 Stunden
        isInParking = false,
        id = UUID.randomUUID().toString(),
        price = 12f,
        description = "",
        image = "",
        amountOfSpots = 10,
        openTime = "10:00",
        closeTime = "23:00"
    )
    ParkingCard(
        parking = sampleParking,
        onCardClick = {},
        onRouteClick = {}
    )
}