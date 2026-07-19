package com.team12.parkquick.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.ui.components.ParkingCard
import com.team12.parkquick.ui.theme.ParkQuickTheme
import com.team12.parkquick.utilities.LocationUtils

@Composable
fun HomeScreen(
    onNavigateToAddParking: () -> Unit,
    parkings: List<ParkingCard>,
    historyParkings: List<ParkingCard>,
    mySpots: List<ParkingCard>, // <--- NEU
    onCardClick: (String) -> Unit
) {
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

            // Aktiver Parkplatz
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

            // ECHTE DATEN: My Spots (Selbst erstellte Spots, Duplikate nach Name gefiltert)
            if (mySpots.isNotEmpty()) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HistoryRow(
                        title = "My Spots",
                        history = mySpots
                            .distinctBy { it.name } // Zeigt jeden Namen nur 1x an
                            .sortedByDescending { it.parkingTimeEnd }
                            .take(5),
                        onCardClick = onCardClick
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // ECHTE DATEN: Last Parking Spots (Chronologische History)
            if (historyParkings.isNotEmpty()) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HistoryRow(
                        title = "Last Parking Spots",
                        history = historyParkings.sortedByDescending { it.parkingTimeEnd }.take(5),
                        onCardClick = onCardClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(88.dp))
        }

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

// ECHTE DATEN ROW (wird jetzt 2x verwendet für "My Spots" und "Last Spots")
@Composable
fun HistoryRow(title: String, history: List<ParkingCard>, onCardClick: (String) -> Unit) {
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
            items(history) { card ->
                HistoryParkingCard(card = card, onClick = { onCardClick(card.id) })
            }
        }
    }
}

// Design optimiert auf 240x160 dp
@Composable
fun HistoryParkingCard(
    card: ParkingCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(160.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(card.image.ifEmpty { null })
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${card.price} €/h",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Opening hours",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${card.openTime} - ${card.closeTime}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
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
            historyParkings = parkings,
            mySpots = parkings,
            onCardClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OhneParkings() {
    ParkQuickTheme {
        HomeScreen(onNavigateToAddParking = {}, listOf(), listOf(), listOf(), onCardClick = {})
    }
}