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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team12.parkquick.data.Parking
import com.team12.parkquick.utilities.TimeFormatter
import java.time.LocalDateTime

@Composable
fun ParkingCard(
    parking: Parking,
    isActive: Boolean,
    onRouteClick: (() -> Unit)? = null
) {
    Card(
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
                    text = TimeFormatter.formatParkingInfo(parking, isActive),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                if (isActive) {
                    Button(onClick = onRouteClick ?: {}) {
                        Text("Route to my car")
                    }
                }
            }
        }
    }
}

val sampleParking = Parking(
    id = "1",
    name = "TH Köln Parkplatz",
    notes = null,
    latitude = 50.0,
    longitude = 7.0,
    imageUrls = emptyList(),
    parkTime = LocalDateTime.now().minusMinutes(25),
    pickupTime = LocalDateTime.now().plusHours(2)
)

@Preview(showBackground = true)
@Composable
fun ParkingCardPreviewActive() {

    ParkingCard(
        parking = sampleParking,
        isActive = true,
        onRouteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ParkingCardPreviewHistory() {

    ParkingCard(
        parking = sampleParking,
        isActive = false,
        onRouteClick = {}
    )
}