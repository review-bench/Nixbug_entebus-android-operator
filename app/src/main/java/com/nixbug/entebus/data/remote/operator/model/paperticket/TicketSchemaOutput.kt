package com.nixbug.entebus.data.remote.operator.model.paperticket

import com.google.gson.annotations.SerializedName

/**
 *
 *
 * @param id
 * @param serviceId
 * @param createdOn
 * @param ticketTypes
 * @param amount
 * @param pickupPoint
 * @param droppingPoint
 * @param distance
 * @param extras
 */


data class TicketSchemaOutput (

    @SerializedName("id")
    val id: Int,

    @SerializedName("service_id")
    val serviceId: Int,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("ticket_types")
    val ticketTypes: List<TicketTypeOutput>,

    @SerializedName("amount")
    val amount: String,

    @SerializedName("pickup_point")
    val pickupPoint: Int,

    @SerializedName("dropping_point")
    val droppingPoint: Int,

    @SerializedName("distance")
    val distance: Int,

    @SerializedName("extras")
    val extras: Map<String, Any>? = null

)
