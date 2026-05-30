package com.team12.parkquick.ui

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ParkingHistory(
    val location: String,
    val date: String,
    val time: String,
    val duration: String
)

@Composable
fun ParkingHistoryScreen() {

    val historyList = listOf(
        ParkingHistory(
            "Cologne City Center",
            "20.05.2026",
            "10:30 - 12:15",
            "1h 45min"
        ),
        ParkingHistory(
            "University Parking",
            "18.05.2026",
            "08:10 - 11:40",
            "3h 30min"
        ),
        ParkingHistory(
            "Shopping Mall",
            "15.05.2026",
            "17:20 - 18:05",
            "45min"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp) // // Abstand, damit die erste Karte im Ruhezustand nicht direkt an der Top Bar klebt
    ) {

        items(historyList) { item ->
            ParkingHistoryCard(item)
        }
    }
}

@Composable
fun ParkingHistoryCard(item: ParkingHistory) {

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
                text = item.location,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Date: ${item.date}")
            Text(text = "Time: ${item.time}")
            Text(text = "Duration: ${item.duration}")
        }
    }
}