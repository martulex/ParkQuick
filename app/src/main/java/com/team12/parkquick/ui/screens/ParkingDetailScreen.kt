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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.ui.components.StaticMapPreview
import com.team12.parkquick.utilities.LocationUtils
import com.team12.parkquick.viewmodels.ParkingViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

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
            }
        )
    }
}

@Composable
fun ParkingDetailContent(
    parkingObj: ParkingCard?,
    onDeleteClick: () -> Unit = {},
    onFinishParkingClick: () -> Unit = {}
) {
    if (parkingObj == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Parking Spot not found")
        }
        return
    }

    val context = LocalContext.current

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
            if (parkingObj.isInParking) {
                Button(
                    onClick = onFinishParkingClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Finish Parking")
                }
            }

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
