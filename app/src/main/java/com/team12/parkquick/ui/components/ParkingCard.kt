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
import com.team12.parkquick.models.Parking
import com.team12.parkquick.utilities.TimeFormatter
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

@Composable
fun ParkingCard(
    parking: Parking, onCardClick : () -> Unit,
    onRouteClick: (() -> Unit)? = null
) {
    var timeLeftString by remember { mutableStateOf(calculateTimeLeft(parking.pickupTime)) }

    // Startet eine reaktive Coroutine, die alle 5 Sekunden die UI aktualisiert
    if (parking.isInParking) {
        LaunchedEffect(parking.pickupTime) {
            while (true) {
                timeLeftString = calculateTimeLeft(parking.pickupTime)
                // Wichtig: Auf 1 Sekunde beschleunigen, damit das Format 0:12:34 "live" tickt!
                kotlinx.coroutines.delay(1000)
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
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Standort"
                )
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (parking.isInParking) "Time remaining: $timeLeftString" else TimeFormatter.formatHistoryInfo(
                        parking
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                if (parking.isInParking) {
                    Button(onClick = onRouteClick ?: {}) {
                        Text("Route to my car")
                    }
                }
            }
        }
    }
}

fun calculateTimeLeft(pickupTime: LocalDateTime): String {
    val duration = Duration.between(LocalDateTime.now(), pickupTime)

    if (duration.isNegative || duration.isZero) {
        return "Expired!"
    }

    val totalSeconds = duration.seconds
    val days = duration.toDays()
    // Stunden verbleibend nach Abzug der vollen Tage
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60
    val seconds = totalSeconds % 60

    return if (days > 0) {
        // Format für mehr als 24 Stunden: "1D 10H 12M"
        "${days}D ${hours}H ${minutes}M"
    } else {
        // Format für weniger als 24 Stunden mit Sekunden-Präzision: "00:12:34"
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingCardPreviewActive() {
    val sampleParking = Parking(
        id = "1",
        name = "TH Köln Parkplatz",
        notes = null,
        latitude = 50.0,
        longitude = 7.0,
        imageUrls = emptyList(),
        parkTime = LocalDateTime.now().minusMinutes(25),
        pickupTime = LocalDateTime.now().plusHours(2),
        isInParking = true
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
    val sampleParking = Parking(
        id = "1",
        name = "TH Köln Parkplatz",
        notes = null,
        latitude = 50.0,
        longitude = 7.0,
        imageUrls = emptyList(),
        parkTime = LocalDateTime.now().minusMinutes(25),
        pickupTime = LocalDateTime.now().plusHours(2),
        isInParking = false
    )
    ParkingCard(
        parking = sampleParking,
        onCardClick = {},
        onRouteClick = {}
    )
}