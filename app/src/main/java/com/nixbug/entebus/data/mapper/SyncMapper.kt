package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.paperticket.AppApiPaperTicketCreateForm
import com.nixbug.entebus.data.remote.operator.model.paperticket.TicketSchemaInput
import com.nixbug.entebus.data.remote.operator.model.paperticket.TicketTypeInput
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceUpdateForm
import com.nixbug.entebus.data.remote.operator.model.service.ServiceStatus
import com.nixbug.entebus.domain.model.PaperTicket
import com.nixbug.entebus.domain.model.ServicePatchStatus
import com.nixbug.entebus.domain.model.ServiceUpdate

fun ServiceUpdate.toDto(): AppApiServiceUpdateForm {

    return AppApiServiceUpdateForm(
        status = when (status) {

            ServicePatchStatus.RESTARTED ->
                ServiceStatus._3

            ServicePatchStatus.FINISHED ->
                ServiceStatus._4
        },
        remark = remark
    )
}

fun PaperTicket.toDto(): AppApiPaperTicketCreateForm {

    return AppApiPaperTicketCreateForm(

        ticket = TicketSchemaInput(

            id = ticket.id,

            serviceId = ticket.serviceId,

            createdOn = ticket.createdOn,

            amount = ticket.amount,

            pickupPoint = ticket.pickupPoint,

            droppingPoint = ticket.droppingPoint,

            distance = ticket.distance,

            extras = ticket.extras,

            ticketTypes = ticket.ticketTypes.map {

                TicketTypeInput(
                    id = it.id,
                    count = it.count,
                    price = it.price
                )
            }
        )
    )
}
