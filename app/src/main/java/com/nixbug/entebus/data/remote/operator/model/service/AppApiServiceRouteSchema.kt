package com.nixbug.entebus.data.remote.operator.model.service

import com.google.gson.annotations.SerializedName

/**
 * Schema for route response.
 *
 * @param serviceId
 * @param landmarkId
 * @param distanceFromStart
 * @param arrivalAt
 * @param departureAt
 */


data class AppApiServiceRouteSchema(

    @SerializedName("service_id")
    val serviceId: Int,

    @SerializedName("landmark_id")
    val landmarkId: Int,

    @SerializedName("distance_from_start")
    val distanceFromStart: Int,

    @SerializedName("arrival_at")
    val arrivalAt: String,

    @SerializedName("departure_at")
    val departureAt: String

)
