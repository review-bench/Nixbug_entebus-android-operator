package com.nixbug.entebus.data.remote.operator.model.paperticket

import com.google.gson.annotations.SerializedName

/**
 *
 *
 * @param id
 * @param count
 * @param price
 */


data class TicketTypeOutput (

    @SerializedName("id")
    val id: Int,

    @SerializedName("count")
    val count: Int,

    @SerializedName("price")
    val price: String

)
