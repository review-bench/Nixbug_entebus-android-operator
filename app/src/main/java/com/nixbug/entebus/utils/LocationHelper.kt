package com.nixbug.entebus.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Utility class for fetching the device's current location using the Fused Location Provider.
 *
 * This class provides a simple interface to request the current location with high accuracy.
 * It handles permission checks externally and returns the result via callbacks.
 *
 * @param context The context used to initialize the FusedLocationProviderClient. Typically, this should be an Activity or Application context.
 *
 * Usage example:
 * ```
 * val locationHelper = LocationHelper(context)
 * locationHelper.fetchCurrentLocation(
 *     onSuccess = { location ->
 *         // Use the location object
 *     },
 *     onError = { errorMsg ->
 *         // Handle the error
 *     }
 * )
 * ```
 */

class LocationHelper(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Fetches the device's current location using the Fused Location Provider.
     *
     * This method requests a single high-accuracy location update. It expects that
     * location permissions (`ACCESS_FINE_LOCATION` and/or `ACCESS_COARSE_LOCATION`)
     * have already been granted by the caller.
     *
     * The `@SuppressLint("MissingPermission")` annotation is used because this method
     * does not perform permission checks internally—it relies on the caller to handle
     * permission validation before invoking this function.
     *
     * @param onSuccess Callback invoked with a valid [Location] object when the
     *                  current location is successfully obtained.
     * @param onError Callback invoked with an error message if the location request
     *                fails or returns `null` (e.g., when GPS is turned off).
     *
     * Behavior:
     * - Requests a high-accuracy location using [CurrentLocationRequest].
     * - Has a timeout duration of 10 seconds.
     * - If no location is retrieved, it triggers `onError`.
     *
     * Possible failure scenarios:
     * - Location services are disabled on the device
     * - Request times out after 10 seconds
     * - Location provider encounters an error
     *
     * @throws SecurityException if permissions are not granted (suppressed via annotation)
     */

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(
        onSuccess: (Location) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .setDurationMillis(10000)
            .build()

        fusedLocationClient.getCurrentLocation(request, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    onSuccess(loc)
                } else {
                    onError("Unable to get current location. Please try again.")
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to get location")
            }
    }
}
