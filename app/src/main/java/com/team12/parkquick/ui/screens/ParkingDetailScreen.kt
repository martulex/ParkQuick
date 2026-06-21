package com.team12.parkquick.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team12.parkquick.utilities.TimeFormatter
import com.team12.parkquick.viewmodels.ParkingViewModel

@Composable
fun ParkingDetailScreen (parkingId : String, viewModel: ParkingViewModel) {

    val parkingObj = viewModel.getParkingByID(parkingId)

    if (parkingObj == null) {
        return Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Parking Spot not found")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(text = parkingObj.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // Map Platzhalter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Map Preview", color = Color.Gray)
            }
        }

        HorizontalDivider(color = Color.LightGray)

        // Info-Bereiche
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Notes:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = parkingObj.notes ?: "No notes provided.")
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Parking Time:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "From: ${TimeFormatter.formatTimeOnly(parkingObj.parkTime)}")
            Text(text = "Until: ${TimeFormatter.formatTimeOnly(parkingObj.pickupTime)}")
        }

        // Spacer drückt die Buttons ganz nach unten an den Bildschirmrand
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Abstand zwischen den Buttons
        ) {

            // Route Button
            Button(
                onClick = { /* Route Logik */ },
                modifier = Modifier.weight(1f) // Nimmt 50% der Breite
            ) {
                Text("Route")
            }

            // Delete Button
            Button(
                onClick = { /* Delete Logik */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        }
    }
}