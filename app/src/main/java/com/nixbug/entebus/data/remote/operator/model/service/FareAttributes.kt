package com.nixbug.entebus.data.remote.operator.model.service

import com.google.gson.annotations.SerializedName

/**
 * Attributes for fare details.
 *
 * @param dfVersion
 * @param ticketTypes
 * @param currencyType
 * @param distanceUnit
 * @param extra
 */


data class FareAttributes(

    @SerializedName("df_version")
    val dfVersion: Int,

    @SerializedName("ticket_types")
    val ticketTypes: List<TicketTypesInAttribute>,

    @SerializedName("currency_type")
    val currencyType: String,

    @SerializedName("distance_unit")
    val distanceUnit: String,

    @SerializedName("extra")
    val extra: Map<String, Any>

)
