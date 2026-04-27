package com.nixbug.entebus.data.remote.operator.model.service

import com.google.gson.annotations.SerializedName

/**
 * Schema for service response with detailed information.
 *
 * @param id
 * @param companyId
 * @param name
 * @param status
 * @param registrationNumber
 * @param startingLandmarkId
 * @param endingLandmarkId
 * @param ticketMode
 * @param remark
 * @param startingAt
 * @param endingAt
 * @param updatedOn
 * @param createdOn
 * @param fare
 * @param vehicle
 * @param route
 * @param publicKey
 */


data class PrivateServiceSchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("registration_number")
    val registrationNumber: String,

    @SerializedName("starting_landmark_id")
    val startingLandmarkId: Int?,

    @SerializedName("ending_landmark_id")
    val endingLandmarkId: Int?,

    @SerializedName("ticket_mode")
    val ticketMode: Int,

    @SerializedName("remark")
    val remark: String?,

    @SerializedName("starting_at")
    val startingAt: String,

    @SerializedName("ending_at")
    val endingAt: String,

    @SerializedName("updated_on")
    val updatedOn: String?,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("fare")
    val fare: AppApiServiceFareSchema,

    @SerializedName("vehicle")
    val vehicle: AppApiServiceVehicleSchema,

    @SerializedName("route")
    val route: List<AppApiServiceRouteSchema>,

    @SerializedName("public_key")
    val publicKey: String

)
