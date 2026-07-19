package com.team12.parkquick.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.team12.parkquick.BuildConfig
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
    // We use the Google Static Maps API to load a PNG instead of a live Map component.
    // This is much more performant in lists and avoids crashes during scrolling.
    val mapUrl = "https://maps.googleapis.com/maps/api/staticmap?" +
            "center=$lat,$lng&zoom=15&size=600x300&markers=color:red%7C$lat,$lng&key=${BuildConfig.MAPS_API_KEY}"

    Image(
        painter = rememberAsyncImagePainter(mapUrl),
        contentDescription = "Map Preview",
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
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
