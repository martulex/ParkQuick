package com.team12.parkquick.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * A static map preview using Google Maps.
 * In Lite Mode, it's efficient for lists.
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

    // Update camera and marker if lat/lng changes
    LaunchedEffect(position) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 15f)
        markerState.position = position
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                scrollGesturesEnabled = false,
                zoomGesturesEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                mapToolbarEnabled = false
            )
        ) {
            Marker(
                state = markerState
            )
        }
    }
}

/**
 * A simple location picker.
 */
@Composable
fun LocationPickerMap(
    initialPosition: LatLng,
    onLocationPicked: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 15f)
    }
    
    val markerState = rememberMarkerState(position = initialPosition)

    // Sync map with initialPosition (e.g. when GPS fixes)
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
        Marker(
            state = markerState,
            title = "Parking Spot"
        )
    }
}
