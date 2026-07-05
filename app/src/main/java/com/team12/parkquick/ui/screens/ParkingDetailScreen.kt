package com.team12.parkquick.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.viewmodels.ParkingViewModel
import java.text.SimpleDateFormat
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = parkingObj.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

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
        val sdf = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.getDefault())
        
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Notes:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = parkingObj.description.ifEmpty { "No notes provided." })
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Parking Time:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "From: ${sdf.format(Date(parkingObj.parkingTimeStart))}")
            Text(text = "Until: ${sdf.format(Date(parkingObj.parkingTimeEnd))}")
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* Route Logik */ },
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
