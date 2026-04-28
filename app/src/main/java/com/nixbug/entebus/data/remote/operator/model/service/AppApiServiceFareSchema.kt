package com.nixbug.entebus.data.remote.operator.model.service

import com.google.gson.annotations.SerializedName

/**
 * Schema for fare response.
 *
 * @param id
 * @param fareId
 * @param version
 * @param name
 * @param attributes
 * @param function
 */


data class AppApiServiceFareSchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("fare_id")
    val fareId: Int,

    @SerializedName("version")
    val version: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("attributes")
    val attributes: FareAttributes,

    @SerializedName("function")
    val function: String

)
