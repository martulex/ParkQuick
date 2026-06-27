package com.team12.parkquick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import com.team12.parkquick.ui.theme.ParkQuickTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.toRoute
import com.team12.parkquick.settings.UserSettingsViewModel
import com.team12.parkquick.ui.navigation.AddParkingRoute
import com.team12.parkquick.ui.navigation.BottomNavItem
import com.team12.parkquick.ui.navigation.HistoryRoute
import com.team12.parkquick.ui.navigation.HomeRoute
import com.team12.parkquick.ui.navigation.ParkingDetailRoute
import com.team12.parkquick.ui.navigation.SettingsRoute
import com.team12.parkquick.ui.screens.AddParkingScreen
import com.team12.parkquick.ui.screens.ParkingHistoryScreen
import com.team12.parkquick.ui.screens.SettingsScreen
import com.team12.parkquick.ui.screens.HomeScreen
import com.team12.parkquick.ui.screens.ParkingDetailScreen
import com.team12.parkquick.viewmodels.ParkingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: UserSettingsViewModel = viewModel()
            val settings by settingsViewModel.settingsState.collectAsStateWithLifecycle()
            ParkQuickTheme(darkTheme = settings.isDarkModeEnabled) {
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

    val parkingViewModel: ParkingViewModel = viewModel()
    val usersettingsViewModel : UserSettingsViewModel = viewModel()
    val activeParkings by parkingViewModel.activeParkings.observeAsState(initial = emptyList())
    val historyParkings by parkingViewModel.historyParkings.observeAsState(initial = emptyList())
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
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding) // Screen fängt unter der TopBar an und hört über der BottomBar auf
        ) {

            composable<HomeRoute> {
                HomeScreen(onNavigateToAddParking = {navController.navigate(AddParkingRoute)}, activeParkings, onCardClick = {navController.navigate(
                    ParkingDetailRoute(parkingId = it))})
            }
            composable<ParkingDetailRoute>{ backStackEntry ->
                val route : ParkingDetailRoute = backStackEntry.toRoute()

                ParkingDetailScreen(
                    parkingId = route.parkingId,
                    viewModel = parkingViewModel
                )

            }
            composable<AddParkingRoute> {
                AddParkingScreen(onNavigateBack = {navController.popBackStack()}, parkingViewModel)
            }
            composable<HistoryRoute> {
                ParkingHistoryScreen(historyParkings, onCardClick = {navController.navigate(
                    ParkingDetailRoute(parkingId = it))})
            }

            composable<SettingsRoute> {
                SettingsScreen(usersettingsViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkQuickTopBar(navController: NavHostController) {
    // Aktuellen Stand der Navigation abrufen
    val navBackStackEntry = navController.currentBackStackEntryAsState().value // Der "live aktualisierte" Eintrag der aktuell ganz oben auf dem Backstack Stapel liegt
    val destination = navBackStackEntry?.destination
    val canNavigateBack = navController.previousBackStackEntry != null && (destination?.hasRoute<AddParkingRoute>() == true || destination?.hasRoute<ParkingDetailRoute>() == true)

    CenterAlignedTopAppBar(
        title = {
            val title = when {
                destination?.hasRoute<HomeRoute>() == true -> "Home"
                destination?.hasRoute<AddParkingRoute>() == true -> "Add Parking Spot"
                destination?.hasRoute<HistoryRoute>() == true -> "History"
                destination?.hasRoute<SettingsRoute>() == true -> "Settings"
                destination?.hasRoute<ParkingDetailRoute>() == true -> "Details"
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
        BottomNavItem(HomeRoute, "Home", Icons.Default.Home),
        BottomNavItem(HistoryRoute, "History", Icons.Default.History),
        BottomNavItem(SettingsRoute, "Settings", Icons.Default.Settings)
    )

    NavigationBar {

        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val destination = navBackStackEntry?.destination

        items.forEach { item ->

            NavigationBarItem(
                selected = destination?.hasRoute(item.route::class) ?: false,
                onClick = {

                    if (destination?.hasRoute(item.route::class) == false) { // verhindert flackerndes nachladen wenn man schon auf dem screen ist

                    navController.navigate(item.route) {

                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        } /* // Räumt den Stack bis zum Homescreen auf, damit sich der Speicher nicht füllt.
                                Alles zwischen dem Homescreen und dem neuen Ziel wird vom Stack gelöscht.
                        mit saveState wird der Zustand des screens gespeichert (z.B. Scrollposition)*/

                        launchSingleTop = true // verhindert, dass das gleiche Ziel mehrfach oben auf dem Stack landet
                        restoreState = true // screen wird wieder so aufgerufen wie man ihn verlassen hat (scrollposition etc.)
                    }
                }},
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

@Preview(showBackground = true)
@Composable
fun ParkQuickAppPreview() {
    ParkQuickTheme {
        ParkQuickApp()
    }
}
