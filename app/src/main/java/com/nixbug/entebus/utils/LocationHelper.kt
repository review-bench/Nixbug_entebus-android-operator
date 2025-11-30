package com.nixbug.entebus.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationHelper(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(
        onSuccess: (Location) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .setDurationMillis(5000) // timeout
            .build()

        fusedLocationClient.getCurrentLocation(request, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    onSuccess(loc)
                } else {
                    onError("Please Enable GPS.")
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to get location")
            }
    }
}
