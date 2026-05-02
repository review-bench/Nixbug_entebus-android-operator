package com.nixbug.entebus.presentation.screens.ticket.uimapper

import com.nixbug.entebus.domain.model.DetailedTicketType
import com.nixbug.entebus.presentation.screens.ticket.Passenger


fun DetailedTicketType.toUiModel(): Passenger {
    return Passenger(
        id = id,
        name = name,
        passengerCount = if (
            name.equals("Adult", ignoreCase = true)
        ) {
            1
        } else {
            0
        }
    )
}
