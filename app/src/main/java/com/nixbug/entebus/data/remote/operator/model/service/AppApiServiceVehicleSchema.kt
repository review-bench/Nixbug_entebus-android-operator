package com.nixbug.entebus.data.remote.operator.model.service

import com.google.gson.annotations.SerializedName

/**
 * Schema for vehicle response.
 *
 * @param id
 * @param vehicleId
 * @param version
 * @param registrationNumber
 * @param name
 * @param capacity
 */


data class AppApiServiceVehicleSchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("vehicle_id")
    val vehicleId: Int,

    @SerializedName("version")
    val version: Int,

    @SerializedName("registration_number")
    val registrationNumber: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("capacity")
    val capacity: Int

)
