package com.nixbug.entebus.domain.usecase.fare

import com.nixbug.entebus.domain.repository.FareCalculatorRepository
import com.nixbug.entebus.presentation.screens.ticket.Passenger
import javax.inject.Inject

class CalculateTicketFareUseCase @Inject constructor(
    private val fareCalculator: FareCalculatorRepository
) {

    operator fun invoke(
        fareFunction: String,
        passengers: List<Passenger>,
        distanceInMeters: Double
    ): Double {

        fareCalculator.initialize(
            fareFunction
        )

        return passengers
            .filter {
                it.passengerCount > 0
            }
            .sumOf { passenger ->

                val singleFare =
                    fareCalculator.calculate(
                        ticketType = passenger.name,
                        distanceInMeters = distanceInMeters
                    )

                singleFare *
                        passenger.passengerCount
            }
    }
}
