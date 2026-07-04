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

@Composable
fun ParkingDetailScreen(
    parkingId: String,
    viewModel: ParkingViewModel,
    onNavigateBack: () -> Unit // Wichtig für den Delete-Button!
) {
    // 1. States für die asynchrone Datenbankabfrage
    var parkingObj by remember { mutableStateOf<ParkingCard?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 2. Im Hintergrund aus der Datenbank laden
    LaunchedEffect(parkingId) {
        parkingObj = viewModel.getParkingByID(parkingId)
        isLoading = false
    }

    // 3. Lade-Bildschirm anzeigen
    if (isLoading) {
        return Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Zeigt einen schönen Lade-Kreis
        }
    }

    // 4. Fehler-Bildschirm (falls die ID nicht in der DB existiert)
    val card = parkingObj
    if (card == null) {
        return Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Parking Spot not found")
        }
    }

    // Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(text = card.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

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

        // Spacer drückt die Buttons ganz nach unten an den Bildschirmrand
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Route Button
            Button(
                onClick = { /* Route Logik kommt später */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Route")
            }

            // Delete Button MIT Logik!
            Button(
                onClick = {
                    viewModel.deleteParking(card) // Löscht den Eintrag aus der Datenbank
                    onNavigateBack()              // Geht zurück zum Home-Screen
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        }
    }
}