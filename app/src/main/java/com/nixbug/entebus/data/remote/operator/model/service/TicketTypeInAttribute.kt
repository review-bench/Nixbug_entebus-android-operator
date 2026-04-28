package com.nixbug.entebus.data.remote.operator.model.service

import com.google.gson.annotations.SerializedName

/**
 * Schema for ticket types in fare attributes.
 *
 * @param id
 * @param name
 */

data class TicketTypeInAttribute(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String

)
