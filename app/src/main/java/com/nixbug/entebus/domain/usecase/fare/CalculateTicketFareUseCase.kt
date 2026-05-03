package com.nixbug.entebus.domain.usecase.fare

import com.nixbug.entebus.domain.model.DetailedTicketType
import com.nixbug.entebus.domain.repository.FareCalculatorRepository
import javax.inject.Inject

class CalculateTicketFareUseCase @Inject constructor(
    private val fareCalculator: FareCalculatorRepository
) {

    suspend operator fun invoke(
        fareFunction: String,
        passengers: List<DetailedTicketType>,
        distanceInMeters: Float
    ): Double {

        fareCalculator.initialize(
            fareFunction
        )

        return passengers
            .filter {
                it.count > 0
            }
            .sumOf { passenger ->

                val singleFare =
                    fareCalculator.calculate(
                        ticketType = passenger.name,
                        distanceInMeters = distanceInMeters
                    )

                singleFare * passenger.count
            }
    }
}