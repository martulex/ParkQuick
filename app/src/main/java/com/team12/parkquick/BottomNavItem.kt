package com.team12.parkquick

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: Any, // Eventuell später ändern
    val label: String,
    val icon: ImageVector
)