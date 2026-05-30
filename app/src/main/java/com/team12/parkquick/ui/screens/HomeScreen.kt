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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.team12.parkquick.data.Parking
import com.team12.parkquick.ui.components.ParkingCard
import com.team12.parkquick.utilities.TimeFormatter
import java.time.LocalDateTime

@Composable
fun HomeScreen(navController: NavHostController) {

    //Aktuellen Parkplätze Testdaten
    val parkings = remember {
        mutableStateListOf(
            Parking(
                id = "3",
                name = "Bahnhof Gummersbach",
                latitude = 51.0260,
                longitude = 7.5660,
                parkTime = LocalDateTime.now(),
                pickupTime = LocalDateTime.now().plusHours(6)
            )
        )
    }

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

            Button(
                onClick = {
                    navController.navigate("add_parking")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Parking Spot")
            }
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
                    isActive = true,
                    onRouteClick = {
                        // später Navigation / Maps
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate("add_parking")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Parking Spot")
            }
        }
    }

}