package com.nixbug.entebus.data.remote.operator.model.paperticket

import com.google.gson.annotations.SerializedName


/**
 * Schema for paper ticket response.
 *
 * @param id
 * @param serviceId
 * @param dutyId
 * @param companyId
 * @param ticket
 * @param amount
 * @param createdOn
 */


data class PaperTicketSchema (

    @SerializedName("id")
    val id: Int,

    @SerializedName("service_id")
    val serviceId: Int,

    @SerializedName("duty_id")
    val dutyId: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("ticket")
    val ticket: TicketSchemaOutput,

    @SerializedName("amount")
    val amount: String,

    @SerializedName("created_on")
    val createdOn: String

)
