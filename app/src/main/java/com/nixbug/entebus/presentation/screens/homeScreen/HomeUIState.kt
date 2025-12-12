package com.nixbug.entebus.presentation.screens.homeScreen

val sampleHomeUiModel = HomeUiModel(
    id = 252,
    companyId = 2,
    name = "2025-12-06 7:00 AM Mayyanad -> Kollam (KL02BT9268)",
    route = Route(
        id = 1,
        name = "07:00 AM Mayyanadu to Kollam",
        status = 1,
        landmark = listOf(
            RouteLandmark(
                id = 5,
                routeId = 1,
                companyId = 2,
                createdOn = "2025-10-25T05:20:40.586826+00:00",
                updatedOn = null,
                landmarkId = 1,
                arrivalDelta = 1800,
                departureDelta = 1800,
                distanceFromStart = 10000
            ),
            RouteLandmark(
                id = 4,
                routeId = 1,
                companyId = 2,
                createdOn = "2025-10-25T05:20:40.578298+00:00",
                updatedOn = null,
                landmarkId = 2,
                arrivalDelta = 1500,
                departureDelta = 1500,
                distanceFromStart = 7500
            ),
            RouteLandmark(
                id = 2,
                routeId = 1,
                companyId = 2,
                createdOn = "2025-10-25T05:20:40.561716+00:00",
                updatedOn = null,
                landmarkId = 3,
                arrivalDelta = 1200,
                departureDelta = 1200,
                distanceFromStart = 5000
            ),
            RouteLandmark(
                id = 1,
                routeId = 1,
                companyId = 2,
                createdOn = "2025-10-25T05:20:40.549347+00:00",
                updatedOn = null,
                landmarkId = 4,
                arrivalDelta = 480,
                departureDelta = 480,
                distanceFromStart = 2500
            ),
            RouteLandmark(
                id = 3,
                routeId = 1,
                companyId = 2,
                createdOn = "2025-10-25T05:20:40.569985+00:00",
                updatedOn = null,
                landmarkId = 5,
                arrivalDelta = 0,
                departureDelta = 0,
                distanceFromStart = 0
            )
        ),
        companyId = 2,
        createdOn = "2025-10-25T05:20:40.014335+00:00",
        startTime = "01:30:00+00:00",
        updatedOn = "2025-10-25T15:02:38.165839+00:00"
    ),
    fare = Fare(
        id = 1,
        name = "Kollam To Bharanikavu",
        scope = 2,
        version = 2,
        function = "function getFare(ticket_type, distance, extra) { /* javascript code */ }",
        attributes = FareAttributes(
            extra = emptyMap(), dfVersion = 1, ticketTypes = listOf(
                TicketType(1, "Adult"), TicketType(2, "Child")
            ), currencyType = "INR", distanceUnit = "m"
        ),
        companyId = 2,
        createdOn = "2025-10-25T05:14:53.462506+00:00",
        updatedOn = "2025-10-25T05:15:00.375387+00:00"
    ),
    busId = 1,
    ticketMode = 1,
    status = 1,
    startingAt = "2025-12-06T01:30:00Z",
    endingAt = "2025-12-06T02:00:00Z",
    remark = null,
    startedOn = null,
    finishedOn = null,
    updatedOn = null,
    createdOn = "2025-12-06T00:30:00.036496Z",
    scheduleId = 1,
    publicKey = """
        -----BEGIN PUBLIC KEY-----
        MEAwEAYHKoZIzj0CAQYFK4EEAAEDLAAEBHo42yDH5VXI2KWXewj7bNsbYOheBY+Y
        PHxLzzz7/Bv2X8H1Zxm9Gyuy
        -----END PUBLIC KEY-----
    """.trimIndent(),
    regNo = "KL02BT9268"
)

data class HomeUIState(

    val ongoingService: HomeUiModel? = null,

    val assignedServiceList: List<HomeUiModel> = listOf(
        sampleHomeUiModel.copy(
            id = 101,
            name = "2025-12-06 7:00 AM Mayyanad -> Kollam (KL02BT9268)",
            startingAt = "2025-12-06T18:30:00Z",
            endingAt = "2025-12-06T02:00:00Z",
            regNo = "KL02BT9268"
        ),
        sampleHomeUiModel.copy(
            id = 102,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
    ),

    val downloadedServiceList: List<HomeUiModel> = listOf(),

    val otherServiceList: List<HomeUiModel> = listOf(
        sampleHomeUiModel.copy(
            id = 201,
            name = "2025-12-06 06:30 AM Kollam -> Paravur (KL02BT1111)",
            startingAt = "2025-12-05T16:30:00Z",
            endingAt = "2025-12-06T01:40:00Z",
            regNo = "KL02BT1111",
            route = sampleHomeUiModel.route.copy(
                name = "06:30 AM Kollam to Paravur"
            )
        ),
        sampleHomeUiModel.copy(
            id = 202,
            name = "2025-12-06 08:00 AM Kollam -> Chathannoor (KL02BT2222)",
            startingAt = "2025-12-08T16:30:00Z",
            endingAt = "2025-12-06T03:10:00Z",
            regNo = "KL02BT2222",
            route = sampleHomeUiModel.route.copy(
                name = "08:00 AM Kollam to Chathannoor"
            )
        ),
        sampleHomeUiModel.copy(
            id = 203,
            name = "2025-12-06 09:15 AM Kollam -> Punalur (KL02BT3333)",
            startingAt = "2025-12-09T16:30:00Z",
            endingAt = "2025-12-06T04:50:00Z",
            regNo = "KL02BT3333",
            route = sampleHomeUiModel.route.copy(
                name = "09:15 AM Kollam to Punalur"
            )
        ),
        sampleHomeUiModel.copy(
            id = 204,
            name = "2025-12-06 11:00 AM Kollam -> Kottarakkara (KL02BT4444)",
            startingAt = "2025-12-06T05:30:00Z",
            endingAt = "2025-12-06T06:20:00Z",
            regNo = "KL02BT4444",
            route = sampleHomeUiModel.route.copy(
                name = "11:00 AM Kollam to Kottarakkara"
            )
        ),
        sampleHomeUiModel.copy(
            id = 205,
            name = "2025-12-06 04:30 PM Kollam -> Karunagappally (KL02BT5555)",
            startingAt = "2025-12-06T11:00:00Z",
            endingAt = "2025-12-06T12:00:00Z",
            regNo = "KL02BT5555",
            route = sampleHomeUiModel.route.copy(
                name = "04:30 PM Kollam to Karunagappally"
            )
        ),
    )
)
