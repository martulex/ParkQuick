package com.team12.parkquick.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.ui.theme.ParkQuickTheme
import com.team12.parkquick.viewmodels.DatabaseViewModel
import java.util.UUID
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import com.team12.parkquick.database.getDistanceInKm
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.filled.Search

@Composable
fun DatabaseScreen(
    viewModel: DatabaseViewModel,
    onCardClick: (String) -> Unit
) {
    // Daten aus dem ViewModel sammeln
    val cards by viewModel.parkingCards.collectAsStateWithLifecycle()

    DatabaseScreenContent(
        cards = cards,
        onAddCardClick = { viewModel.addDummyCard() },
        onCardClick = onCardClick
    )
}

// Nur UI
@Composable
fun DatabaseScreenContent(
    cards: List<ParkingCard>,
    onAddCardClick: () -> Unit,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Dieser State merkt sich, ob die Suchleiste gerade sichtbar ist oder nicht
    var showSearchArea by remember { mutableStateOf(false) }
    // Dummy-States für die Vorschau der Textfelder (später kommen die aus dem ViewModel)
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp) // Abstand zwischen den Buttons
            ) {
                // 1. Der neue, schwebende Such-Button (etwas kleiner)
                FloatingActionButton(
                    onClick = { showSearchArea = !showSearchArea }, // Toggelt die Sichtbarkeit
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Suchen & Filtern")
                }
                FloatingActionButton(
                    onClick = onAddCardClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Hinzufügen", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = showSearchArea,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true
                    )

                    // Platzhalter für zukünftige Filter-Chips (Privat / Community etc.)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = true, onClick = { }, label = { Text("Filter A") })
                        FilterChip(selected = false, onClick = { }, label = { Text("Filter B") })
                    }
                }
            }

            // Die Liste der Parkplätze
            if (cards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No parking spot data", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp) // Extra Platz unten wegen der Buttons!
                ) {
                    items(cards) { card ->
                        ParkingCardItem(card = card, onClick = { onCardClick(card.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingCardItem(
    card: ParkingCard,
    onClick: () -> Unit,
    // FÜR SPÄTER: Hier kommt irgendwann das echte GPS vom Handy rein.
    // Für jetzt tun wir so, als stünde der Nutzer an diesen Koordinaten (z.B. Köln Zentrum).
    userLat: Double = 50.9375,
    userLon: Double = 6.9603,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. Das Bild (Unverändert)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(card.image.ifEmpty { null })
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Vorschau",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Die Texte und neuen Icons
            Column(modifier = Modifier.weight(1f)) {

                // Name & Preis
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${card.price} € / Stunde",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Uhrzeit-Block
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Öffnungszeiten",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${card.openTime} bis ${card.closeTime}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Entfernungs-Block
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Entfernung",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = card.getDistanceInKm(userLat, userLon),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Empty List", showSystemUi = true)
@Composable
fun DatabaseScreenPreviewEmpty() {
    ParkQuickTheme {
        DatabaseScreenContent(
            cards = emptyList(),
            onAddCardClick = {},
            onCardClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Interactive List", showSystemUi = true)
@Composable
fun DatabaseScreenPreviewPopulated() {
    val startCards = listOf(
        ParkingCard(
            id = UUID.randomUUID().toString(),
            name = "Parkhaus am Dom",
            price = 2.50f,
            description = "Zentrales Parkhaus.",
            image = "",
            latitude = 50.9,
            longitude = 6.9,
            amountOfSpots = 150,
            openTime = "00:00",
            closeTime = "23:59"
        )
    )
    var cardList by remember { mutableStateOf(startCards) }

    ParkQuickTheme {
        DatabaseScreenContent(
            cards = cardList,
            onAddCardClick = {
                val newFakeCard = ParkingCard(
                    id = UUID.randomUUID().toString(),
                    name = "Neuer Parkplatz ${cardList.size + 1}",
                    price = 1.50f,
                    description = "Gerade in der Preview hinzugefügt!",
                    image = "",
                    latitude = 50.0,
                    longitude = 6.0,
                    amountOfSpots = 10,
                    openTime = "08:00",
                    closeTime = "18:00"
                )
                // Fügt die neue Karte zur Liste hinzu, woraufhin die UI sich neu zeichnet
                cardList = cardList + newFakeCard
            },
            onCardClick = {}
        )
    }
}