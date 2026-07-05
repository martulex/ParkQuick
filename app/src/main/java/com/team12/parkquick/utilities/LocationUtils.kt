package com.team12.parkquick.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

object LocationUtils {
    /**
     * Opens the Google Maps app and starts navigation to the specified coordinates.
     */
    fun openNavigation(context: Context, latitude: Double, longitude: Double) {
        // Mode "w" stands for walking. This opens Google Maps with walking directions.
        val gmmIntentUri = "google.navigation:q=$latitude,$longitude&mode=w".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        
        // Try to specifically use Google Maps if available
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            // Fallback: If Google Maps app is not installed or fails, try a generic geo intent
            val fallbackUri = "geo:$latitude,$longitude?q=$latitude,$longitude".toUri()
            val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)
            try {
                context.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                Toast.makeText(context, "No map application found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
