package com.team12.parkquick.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team12.parkquick.models.Parking
import com.team12.parkquick.ui.theme.ParkQuickTheme
import com.team12.parkquick.ui.components.ParkingCard
import com.team12.parkquick.utilities.TimeFormatter
import java.time.LocalDateTime

@Composable
fun ParkingHistoryScreen(historyList: List<Parking>) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp) // // Abstand, damit die erste Karte im Ruhezustand nicht direkt an der Top Bar klebt
    ) {

        items(historyList) { item ->
            ParkingCard(
                parking = item,
                onCardClick = {},
                onRouteClick = {}
            )
        }
    }
}

@Composable
fun ParkingHistoryCard(item: Parking) {

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    .background(
                        Color(0xFFDDEAF6),
                        RoundedCornerShape(12.dp)
                    ),
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

            Text(text = "Park time: ${item.parkTime}")
            Text(text = "Pickup time: ${item.pickupTime}")
            Text(text = "Duration: ${TimeFormatter.formatRemainingTime(item.parkTime, item.pickupTime)}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingHistoryPreview() {
    ParkQuickTheme {
        ParkingHistoryScreen(
            listOf(
                Parking(
                    id = "1",
                    name = "Flughafen Köln",
                    latitude = 50.8659,
                    longitude = 7.1427,
                    parkTime = LocalDateTime.now(),
                    pickupTime = LocalDateTime.now().plusDays(2),
                    isInParking = false
                ),
                Parking(
                    id = "2",
                    name = "Somewhere",
                    latitude = 50.8659,
                    longitude = 7.1427,
                    parkTime = LocalDateTime.now(),
                    pickupTime = LocalDateTime.now().plusDays(2),
                    isInParking = false
                ),
                Parking(
                    id = "3",
                    name = "Campus Parking",
                    latitude = 50.8659,
                    longitude = 7.1427,
                    parkTime = LocalDateTime.now(),
                    pickupTime = LocalDateTime.now().plusDays(2),
                    isInParking = false
                )

            )
        )
    }
}