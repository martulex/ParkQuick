package com.team12.parkquick.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.ui.components.ParkingCard
import com.team12.parkquick.ui.theme.ParkQuickTheme
import com.team12.parkquick.utilities.LocationUtils

@Composable
fun HomeScreen(onNavigateToAddParking: () -> Unit, parkings: List<ParkingCard>, onCardClick: (String) -> Unit) {
    val context = LocalContext.current
    val hasActiveParking = parkings.any { it.isInParking }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "My Parking",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
            )

            if(parkings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Active Spot",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    parkings.forEach { parking ->
                        ParkingCard(
                            parking = parking,
                            onRouteClick = {
                                LocationUtils.openNavigation(context, parking.latitude, parking.longitude)
                            },
                            onCardClick = { onCardClick(parking.id) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                PlaceholderRow(title = "My Spots")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                PlaceholderRow(title = "Last Parking Spots")
            }

            Spacer(modifier = Modifier.height(88.dp))
        }


        // Versteckt sich automatisch, wenn ein aktiver Parkplatz vorhanden ist
        if (!hasActiveParking) {
            FloatingActionButton(
                onClick = onNavigateToAddParking,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Parking Spot",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun PlaceholderRow(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(5) { index ->
                Card(
                    modifier = Modifier
                        .width(240.dp)
                        .height(160.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Spot ${index + 1}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MitParkings() {
    val parkings = listOf(
        ParkingCard(
            id = "3",
            name = "Bahnhof Gummersbach",
            latitude = 51.0260,
            longitude = 7.5660,
            parkingTimeStart = System.currentTimeMillis(),
            parkingTimeEnd = System.currentTimeMillis() + (6 * 60 * 60 * 1000),
            isInParking = true,
            price = 4.50f,
            description = "Hallo",
            image = "",
            amountOfSpots = 12,
            openTime = "12:00",
            closeTime = "22:00"
        )
    )

    ParkQuickTheme {
        HomeScreen(
            onNavigateToAddParking = {},
            parkings = parkings,
            onCardClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OhneParkings() {
    ParkQuickTheme {
        HomeScreen(onNavigateToAddParking = {}, listOf(), onCardClick = {})
    }
}