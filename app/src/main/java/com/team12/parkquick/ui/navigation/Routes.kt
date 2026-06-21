package com.team12.parkquick.ui.navigation

import kotlinx.serialization.Serializable

// Routes for NavHost
@Serializable
class ParkingDetailRoute(val parkingId : String)
@Serializable
object HomeRoute

@Serializable
object AddParkingRoute

@Serializable
object HistoryRoute

@Serializable
object SettingsRoute