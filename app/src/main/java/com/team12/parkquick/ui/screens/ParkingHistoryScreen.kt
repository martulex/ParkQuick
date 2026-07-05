package com.team12.parkquick.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.ui.theme.ParkQuickTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Composable
fun ParkingHistoryScreen(historyList: List<ParkingCard>, onCardClick: (String) -> Unit) {

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Parking History.",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Deine bisherige Liste
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
        ) {
            items(historyList) { item ->
                ParkingHistoryCard(
                    item = item,
                    onClick = { onCardClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun ParkingHistoryCard(item: ParkingCard, onClick: () -> Unit) { // 1. Auf ParkingCard geändert

    // 2. Datum-Formatierer für unsere Long-Zahlen (Millisekunden)
    val timeFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    // 3. Dauer berechnen (Differenz in Millisekunden umrechnen in Stunden & Minuten)
    val durationMillis = item.parkingTimeEnd - item.parkingTimeStart
    val hours = (durationMillis / (1000 * 60 * 60)).toInt()
    val minutes = ((durationMillis / (1000 * 60)) % 60).toInt()
    val durationText = "${hours}h ${minutes}m"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Klickbar gemacht!
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFDDEAF6), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Map Preview",
                    fontSize = 18.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Park time: ${timeFormatter.format(Date(item.parkingTimeStart))}")
            Text(text = "Pickup time: ${timeFormatter.format(Date(item.parkingTimeEnd))}")
            Text(text = "Duration: $durationText")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingHistoryPreview() {
    ParkQuickTheme {
        ParkingHistoryScreen(
            historyList = listOf(
                // 5. Preview nutzt jetzt die neue ParkingCard mit Millisekunden
                ParkingCard(
                    id = "1",
                    name = "Flughafen Köln",
                    latitude = 50.8659,
                    longitude = 7.1427,
                    // Start war vor 2 Tagen (48 Stunden in Millisekunden)
                    parkingTimeStart = System.currentTimeMillis() - (48 * 60 * 60 * 1000),
                    parkingTimeEnd = System.currentTimeMillis(),
                    isInParking = false,
                    price = 23f,
                    description = "",
                    image = "",
                    amountOfSpots = 4,
                    openTime = "00:00",
                    closeTime = "12:00"
                ),
                ParkingCard(
                    id = "2",
                    name = "Campus Parking",
                    latitude = 50.8659,
                    longitude = 7.1427,
                    // Start war vor 5 Stunden
                    parkingTimeStart = System.currentTimeMillis() - (5 * 60 * 60 * 1000),
                    parkingTimeEnd = System.currentTimeMillis(),
                    isInParking = false,
                    price = 12f,
                    description = "",
                    image = "",
                    amountOfSpots = 2,
                    openTime = "12:00",
                    closeTime = "23:00"
                )
            ),
            onCardClick = {}
        )
    }
}