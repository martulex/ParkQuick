package com.team12.parkquick.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.team12.parkquick.database.ParkingCard

/**
 * A static map preview using Google Maps.
 */
@Composable
fun StaticMapPreview(
    lat: Double,
    lng: Double,
    modifier: Modifier = Modifier
) {
    val position = remember(lat, lng) { LatLng(lat, lng) }
    val cameraPositionState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(position, 15f)
    }
    val markerState = rememberMarkerState(position = position)

    LaunchedEffect(position) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 15f)
        markerState.position = position
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                scrollGesturesEnabled = false,
                zoomGesturesEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                mapToolbarEnabled = false
            )
        ) {
            Marker(state = markerState)
        }
    }
}

/**
 * A location picker that also shows existing parking spots.
 */
@Composable
fun LocationPickerMap(
    initialPosition: LatLng,
    existingSpots: List<ParkingCard> = emptyList(),
    onLocationPicked: (LatLng) -> Unit,
    onSpotSelected: (ParkingCard) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 15f)
    }
    
    val markerState = rememberMarkerState(position = initialPosition)

    LaunchedEffect(initialPosition) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(initialPosition, 15f))
        markerState.position = initialPosition
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            markerState.position = latLng
            onLocationPicked(latLng)
        }
    ) {
        // Main Marker for selected position (Red)
        Marker(
            state = markerState,
            title = "Selected Location",
            snippet = "New parking spot here",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )

        // Markers for existing spots (Azure)
        existingSpots.forEach { spot ->
            Marker(
                state = rememberMarkerState(position = LatLng(spot.latitude, spot.longitude)),
                title = "${spot.price}€/h",
                snippet = spot.name,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                onClick = {
                    onSpotSelected(spot)
                    markerState.position = LatLng(spot.latitude, spot.longitude) // Move main marker to selected spot
                    false // Return false to show info window with price
                }
            )
        }
    }
}
