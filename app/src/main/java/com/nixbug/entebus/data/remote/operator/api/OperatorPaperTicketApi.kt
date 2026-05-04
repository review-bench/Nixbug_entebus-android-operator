package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.paperticket.AppApiPaperTicketCreateForm
import com.nixbug.entebus.data.remote.operator.model.paperticket.PaperTicketSchema
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OperatorPaperTicketApi {

    /**
     * POST company/service/ticket/paper
     * Create Paper Ticket Operator
     * **Creates a new paper ticket for an operator&#39;s duty.**                 - Requires a valid operator access token.                 - Logged-in operator must have &#x60;company.service.ticket.create&#x60; permission.                 - Service must belong to the operator&#39;s company.                 - If no active duty exists, a new duty is created automatically.                 - If a duty is ENDED, it is reactivated to STARTED with &#x60;finished_on&#x60; cleared.                 - A duty in AUDITED status is ignored; a new duty is created instead.                 - &#x60;ticket.pickup_point&#x60; and &#x60;ticket.dropping_point&#x60; must be landmarks assigned to the service.                 - Ticket type IDs must match those defined in the service fare configuration.                 - Prices are cross-validated server-side using the fare function.                 - &#x60;amount&#x60; must equal the sum of (price × count) for all ticket types.
     * Responses:
     *  - 201: Successful Response
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *  - 406: Not Acceptable
     *  - 422: Validation Error
     *
     * @param appApiPaperTicketCreateForm
     * @return [PaperTicketSchema]
     */
    @POST("company/service/ticket/paper")
    suspend fun createPaperTicket(@Body appApiPaperTicketCreateForm: AppApiPaperTicketCreateForm): Response<PaperTicketSchema>
}