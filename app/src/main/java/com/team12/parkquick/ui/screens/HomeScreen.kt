package com.team12.parkquick.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.ui.components.ParkingCard
import com.team12.parkquick.ui.theme.ParkQuickTheme
import com.team12.parkquick.utilities.LocationUtils
import java.time.LocalDateTime

@Composable
fun HomeScreen(onNavigateToAddParking: () -> Unit, parkings: List<ParkingCard>, onCardClick: (String) -> Unit) {
    val context = LocalContext.current

    if(parkings.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ParkQuick",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            AddParkingButton(onNavigateToAddParking)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            parkings.forEach { parking ->

                ParkingCard(
                    parking = parking,
                    onRouteClick = {
                        LocationUtils.openNavigation(context, parking.latitude, parking.longitude)
                    },
                    onCardClick = {onCardClick(parking.id)},
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            AddParkingButton(onNavigateToAddParking)
        }
    }

}

@Composable
fun AddParkingButton(onNavigateToAddParking: () -> Unit) {
    Button(
        onClick = onNavigateToAddParking,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Add Parking Spot")
    }
}

@Preview(showBackground = true)
@Composable
fun MitParkings() {
    // 1. Eine simple listOf reicht für die Vorschau völlig aus!
    val parkings = listOf(
        ParkingCard(
            id = "3",
            name = "Bahnhof Gummersbach",
            latitude = 51.0260,
            longitude = 7.5660,
            parkingTimeStart = System.currentTimeMillis(),
            parkingTimeEnd = System.currentTimeMillis() + (6 * 60 * 60 * 1000), // + 6 Stunden
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
