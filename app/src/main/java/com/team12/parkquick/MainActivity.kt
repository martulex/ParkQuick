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
import com.team12.parkquick.ui.screens.AddParkingContent
import com.team12.parkquick.ui.screens.ParkingDetailContent
import com.team12.parkquick.ui.screens.ParkingHistoryScreen
import com.team12.parkquick.ui.screens.SettingsScreenContent
import com.team12.parkquick.ui.screens.HomeScreen
import com.team12.parkquick.viewmodels.ParkingViewModel
import com.team12.parkquick.database.ParkingCard

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
fun ParkQuickApp(
    parkingViewModel: ParkingViewModel = viewModel(),
    usersettingsViewModel : UserSettingsViewModel = viewModel()
) {
    val navController = rememberNavController()

    val activeParkings by parkingViewModel.activeParkings.collectAsStateWithLifecycle(emptyList())
    val historyParkings by parkingViewModel.historyParkings.collectAsStateWithLifecycle(emptyList())
    val settings by usersettingsViewModel.settingsState.collectAsStateWithLifecycle()

    ParkQuickAppContent(
        navController = navController,
        activeParkings = activeParkings,
        historyParkings = historyParkings,
        onGetParkingById = { id -> 
            // In-memory lookup from the current state
            activeParkings.find { it.id == id } ?: historyParkings.find { it.id == id }
        },
        onSaveParking = { minutes, name, notes -> parkingViewModel.addNewParking(minutes, name, notes) },
        isDarkModeEnabled = settings.isDarkModeEnabled,
        onToggleDarkMode = { usersettingsViewModel.toggleDarkMode() }
    )
}

@Composable
fun ParkQuickAppContent(
    navController: NavHostController,
    activeParkings: List<ParkingCard>,
    historyParkings: List<ParkingCard>,
    onGetParkingById: (String) -> ParkingCard?,
    onSaveParking: (Long, String, String?) -> Unit,
    isDarkModeEnabled: Boolean,
    onToggleDarkMode: () -> Unit
) {
    Scaffold(
        topBar = {
            ParkQuickTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onNavigateToAddParking = { navController.navigate(AddParkingRoute) },
                    parkings = activeParkings,
                    onCardClick = { navController.navigate(ParkingDetailRoute(parkingId = it)) }
                )
            }
            composable<ParkingDetailRoute> { backStackEntry ->
                val route: ParkingDetailRoute = backStackEntry.toRoute()
                val parkingObj = onGetParkingById(route.parkingId)
                ParkingDetailContent(parkingObj)
            }
            composable<AddParkingRoute> {
                AddParkingContent(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveParking = { minutes, name, notes ->
                        onSaveParking(minutes, name, notes)
                        navController.popBackStack()
                    }
                )
            }
            composable<HistoryRoute> {
                ParkingHistoryScreen(
                    historyList = historyParkings,
                    onCardClick = { navController.navigate(ParkingDetailRoute(parkingId = it)) }
                )
            }
            composable<SettingsRoute> {
                SettingsScreenContent(
                    isDarkModeActive = isDarkModeEnabled,
                    onToggleDarkMode = onToggleDarkMode
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkQuickTopBar(navController: NavHostController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val destination = navBackStackEntry?.destination
    val canNavigateBack = navController.previousBackStackEntry != null && 
        (destination?.hasRoute<AddParkingRoute>() == true || destination?.hasRoute<ParkingDetailRoute>() == true)

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
                        contentDescription = "Back"
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
                    if (destination?.hasRoute(item.route::class) == false) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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

@Preview(showBackground = true)
@Composable
fun ParkQuickAppPreview() {
    val navController = rememberNavController()
    val sampleParkings = listOf(
        ParkingCard(
            id = "1",
            name = "Cinema Parking",
            description = "Near entrance",
            parkingTimeStart = System.currentTimeMillis(),
            parkingTimeEnd = System.currentTimeMillis() + (2 * 60 * 60 * 1000),
            isInParking = true,
            latitude = 0.0,
            longitude = 0.0,
            price = 0f,
            amountOfSpots = 1,
            openTime = "00:00",
            closeTime = "23:59"
        )
    )
    ParkQuickTheme {
        ParkQuickAppContent(
            navController = navController,
            activeParkings = sampleParkings,
            historyParkings = emptyList(),
            onGetParkingById = { id -> sampleParkings.find { it.id == id } },
            onSaveParking = { _, _, _ -> },
            isDarkModeEnabled = false,
            onToggleDarkMode = {}
        )
    }
}
