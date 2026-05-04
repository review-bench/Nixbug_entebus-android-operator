package com.nixbug.entebus.domain.model

data class PaperTicket(
    val ticket: Ticket
)

data class Ticket(
    val id: Int,
    val serviceId: Int,
    val createdOn: String,
    val ticketTypes: List<PaperTicketType>,
    val amount: Double,
    val pickupPoint: Int,
    val droppingPoint: Int,
    val distance: Int,
    val extras: Map<String, String>
)

data class PaperTicketType(
    val id: Int,
    val count: Int,
    val price: Double
)
