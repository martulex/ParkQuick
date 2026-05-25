package com.team12.parkquick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.team12.parkquick.ui.theme.ParkQuickTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParkQuickTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ParkQuickApp()
                }
            }
        }
    }
}

@Composable
fun ParkQuickApp() {
    // navController für die Navigation im System (remember um auch bei Änderungen (z.B. drehen des Bildschirms, "neu zeichnen" des Canvas
    // zu wissen, wo man ist und wohin man gehen kann)
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            ParkQuickTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding -> // Lokale Variable die automatisch das padding der Scaffold Elemente enthält (Kotlin Regeln)
        // und dann der nächsten Zeile weitergegeben werden kann.

        // Über den NavHost wird immer der Bildschirm eingeblendet, den der navController auswählt.

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding) // Screen fängt unter der TopBar an und hört über der BottomBar auf
        ) {

            composable("home") {
                HomeScreen(navController)
            }
            composable("add_parking") {
                AddParkingScreen(navController)
            }
            composable("history") {
                HistoryScreen()
            }

            composable("settings") {
                SettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkQuickTopBar(navController: NavHostController) {
    // Aktuellen Stand der Navigation abrufen
    val navBackStackEntry = navController.currentBackStackEntryAsState().value // Der "live aktualisierte" Eintrag der aktuell ganz oben auf dem Backstack Stapel liegt
    val currentRoute = navBackStackEntry?.destination?.route // Name der aktuellen Seite
    val canNavigateBack = navController.previousBackStackEntry != null && currentRoute == "add_parking" // Bestimmt, ob man zurückgehen kann oder nicht (wichtig für den Pfeil)

    CenterAlignedTopAppBar(
        title = {
            val title = when (currentRoute) {
                "home" -> "Home"
                "add_parking" -> "Add Parking Spot"
                "history" -> "History"
                "settings" -> "Settings"
                else -> "ParkQuick"
            }
            Text(text = title)
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back" // Accessibility
                    )
                }
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    val items = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("history", "History", Icons.Default.History),
        BottomNavItem("settings", "Settings", Icons.Default.Settings)
    )

    NavigationBar {

        val currentRoute =
            navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {

                        popUpTo(navController.graph.startDestinationId) // Verhindern das sich der Speicher mit unendlich vielen alten Screens füllt, wenn man zum Beispiel mehrmals auf History oder hin und her klickt.
                        launchSingleTop = true // gleiche auswahl / screens werden nicht mehrfach übereinander geworfen
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParkingScreen(navController: NavHostController) {
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Location: Karte wird direkt als Vorschau angezeigt
        Text(text = "Location", style = MaterialTheme.typography.titleMedium)

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

        // Timer: Schnellauswahl für Zeiten
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Text(text = "Set Timer", style = MaterialTheme.typography.titleMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(onClick = { /* Set 30 min */ }, label = { Text("30m") })
                AssistChip(onClick = { /* Set 1h */ }, label = { Text("1h") })
                AssistChip(onClick = { /* Set 2h */ }, label = { Text("2h") })
                AssistChip(onClick = { /* Set Custom Timer */ }, label = { Text("Custom")})
            }
        }

        // Photo Sektion
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Photo", style = MaterialTheme.typography.titleMedium)

                OutlinedButton(
                    onClick = { /* Open Camera */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Take Photo")
                }

        }

        // 4. NOTES
        OutlinedTextField(
            value = notes, // Das Textfeld schaut in die Merkzelle notes und zeigt dem Nutzer immer genau den Text an, der dort gerade abgespeichert ist.
            onValueChange = { notes = it }, // Jedes Mal, wenn der Nutzer eine Taste drückt, fängt Android den neuen Text ab (it) und speichert ihn sofort in notes ab, damit der Bildschirm sich mit dem neuen Buchstaben aktualisieren kann.
            label = { Text("Notes (e.g. Floor, Pillar number)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bestätigen
        Button(
            onClick = {
                // TODO: Daten speichern
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Parking Spot")
        }
    }
}
@Composable
fun HistoryScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "History Screen",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun SettingsScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "Settings Screen",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ParkQuickAppPreview() {
    ParkQuickTheme {
        ParkQuickApp()
    }
}