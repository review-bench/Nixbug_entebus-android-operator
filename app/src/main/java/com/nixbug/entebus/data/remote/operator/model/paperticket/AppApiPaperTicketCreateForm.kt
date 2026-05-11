package com.nixbug.entebus.data.remote.operator.model.paperticket

import com.google.gson.annotations.SerializedName

/**
 * Form data for creating a new paper ticket.
 *
 * @param ticket
 */

data class AppApiPaperTicketCreateForm(
    @SerializedName("ticket")
    val ticket: TicketSchemaInput
)
