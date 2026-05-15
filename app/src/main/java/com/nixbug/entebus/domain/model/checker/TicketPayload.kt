package com.nixbug.entebus.domain.model.checker

import com.google.gson.annotations.SerializedName

data class TicketPayload(

    @SerializedName("ticket")
    val ticket: Ticket
)

data class Ticket(

    @SerializedName("id")
    val id: Int,

    @SerializedName("service_id")
    val serviceId: Int,

    @SerializedName("pickup_point")
    val pickupPoint: Int,

    @SerializedName("dropping_point")
    val droppingPoint: Int,

    @SerializedName("ticket_types")
    val ticketTypes: List<TicketTypePayload>
)

data class TicketTypePayload(

    @SerializedName("id")
    val id: Int,

    @SerializedName("count")
    val count: Int,

    @SerializedName("price")
    val price: Double
)
