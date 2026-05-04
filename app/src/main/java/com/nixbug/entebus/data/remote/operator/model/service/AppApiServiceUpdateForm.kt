package com.nixbug.entebus.data.remote.operator.model.service

import com.google.gson.annotations.SerializedName

/**
 * Form data for updating an existing service.
 *
 * @param ticketMode HYBRID: 1, DIGITAL: 2, CONVENTIONAL: 3
 * @param status CREATED: 1, CACHED: 2, STARTED: 3, ENDED: 4, AUDITED: 5
 * @param remark
 */


data class AppApiServiceUpdateForm (

    /* HYBRID: 1, DIGITAL: 2, CONVENTIONAL: 3 */
    @SerializedName("ticket_mode")
    val ticketMode: TicketingMode? = null,

    /* CREATED: 1, CACHED: 2, STARTED: 3, ENDED: 4, AUDITED: 5 */
    @SerializedName("status")
    val status: ServiceStatus? = null,

    @SerializedName("remark")
    val remark: String? = null

)
