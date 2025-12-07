package com.nixbug.entebus.presentation.screens.homeScreen

val sampleHomeUiModel = HomeUiModel(
    id = 252,
    company_id = 2,
    name = "2025-12-06 7:00 AM Mayyanad -> Kollam (KL02BT9268)",
    route = Route(
        id = 1,
        name = "07:00 AM Mayyanadu to Kollam",
        status = 1,
        landmark = listOf(
            RouteLandmark(
                id = 5,
                route_id = 1,
                company_id = 2,
                created_on = "2025-10-25T05:20:40.586826+00:00",
                updated_on = null,
                landmark_id = 1,
                arrival_delta = 1800,
                departure_delta = 1800,
                distance_from_start = 10000
            ), RouteLandmark(
                id = 4,
                route_id = 1,
                company_id = 2,
                created_on = "2025-10-25T05:20:40.578298+00:00",
                updated_on = null,
                landmark_id = 2,
                arrival_delta = 1500,
                departure_delta = 1500,
                distance_from_start = 7500
            ), RouteLandmark(
                id = 2,
                route_id = 1,
                company_id = 2,
                created_on = "2025-10-25T05:20:40.561716+00:00",
                updated_on = null,
                landmark_id = 3,
                arrival_delta = 1200,
                departure_delta = 1200,
                distance_from_start = 5000
            ), RouteLandmark(
                id = 1,
                route_id = 1,
                company_id = 2,
                created_on = "2025-10-25T05:20:40.549347+00:00",
                updated_on = null,
                landmark_id = 4,
                arrival_delta = 480,
                departure_delta = 480,
                distance_from_start = 2500
            ), RouteLandmark(
                id = 3,
                route_id = 1,
                company_id = 2,
                created_on = "2025-10-25T05:20:40.569985+00:00",
                updated_on = null,
                landmark_id = 5,
                arrival_delta = 0,
                departure_delta = 0,
                distance_from_start = 0
            )
        ),
        company_id = 2,
        created_on = "2025-10-25T05:20:40.014335+00:00",
        start_time = "01:30:00+00:00",
        updated_on = "2025-10-25T15:02:38.165839+00:00"
    ),
    fare = Fare(
        id = 1,
        name = "Kollam To Bharanikavu",
        scope = 2,
        version = 2,
        function = "function getFare(ticket_type, distance, extra) { /* javascript code */ }",
        attributes = FareAttributes(
            extra = emptyMap(), df_version = 1, ticket_types = listOf(
                TicketType(1, "Adult"), TicketType(2, "Child")
            ), currency_type = "INR", distance_unit = "m"
        ),
        company_id = 2,
        created_on = "2025-10-25T05:14:53.462506+00:00",
        updated_on = "2025-10-25T05:15:00.375387+00:00"
    ),
    bus_id = 1,
    ticket_mode = 1,
    status = 1,
    starting_at = "2025-12-06T01:30:00Z",
    ending_at = "2025-12-06T02:00:00Z",
    remark = null,
    started_on = null,
    finished_on = null,
    updated_on = null,
    created_on = "2025-12-06T00:30:00.036496Z",
    schedule_id = 1,
    public_key = """
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
            starting_at = "2025-12-06T18:30:00Z",
            ending_at = "2025-12-06T02:00:00Z",
            regNo = "KL02BT9268"
        ),
        sampleHomeUiModel.copy(
            id = 102,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            starting_at = "2025-12-07T06:30:00Z",
            ending_at = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
    ),

    val downloadedServiceList: List<HomeUiModel> = listOf(),

    val otherServiceList: List<HomeUiModel> = listOf(
        sampleHomeUiModel.copy(
            id = 201,
            name = "2025-12-06 06:30 AM Kollam -> Paravur (KL02BT1111)",
            starting_at = "2025-12-05T16:30:00Z",
            ending_at = "2025-12-06T01:40:00Z",
            regNo = "KL02BT1111",
            route = sampleHomeUiModel.route.copy(
                name = "06:30 AM Kollam to Paravur"
            )
        ),
        sampleHomeUiModel.copy(
            id = 202,
            name = "2025-12-06 08:00 AM Kollam -> Chathannoor (KL02BT2222)",
            starting_at = "2025-12-08T16:30:00Z",
            ending_at = "2025-12-06T03:10:00Z",
            regNo = "KL02BT2222",
            route = sampleHomeUiModel.route.copy(
                name = "08:00 AM Kollam to Chathannoor"
            )
        ),
        sampleHomeUiModel.copy(
            id = 203,
            name = "2025-12-06 09:15 AM Kollam -> Punalur (KL02BT3333)",
            starting_at = "2025-12-09T16:30:00Z",
            ending_at = "2025-12-06T04:50:00Z",
            regNo = "KL02BT3333",
            route = sampleHomeUiModel.route.copy(
                name = "09:15 AM Kollam to Punalur"
            )
        ),
        sampleHomeUiModel.copy(
            id = 204,
            name = "2025-12-06 11:00 AM Kollam -> Kottarakkara (KL02BT4444)",
            starting_at = "2025-12-06T05:30:00Z",
            ending_at = "2025-12-06T06:20:00Z",
            regNo = "KL02BT4444",
            route = sampleHomeUiModel.route.copy(
                name = "11:00 AM Kollam to Kottarakkara"
            )
        ),
        sampleHomeUiModel.copy(
            id = 205,
            name = "2025-12-06 04:30 PM Kollam -> Karunagappally (KL02BT5555)",
            starting_at = "2025-12-06T11:00:00Z",
            ending_at = "2025-12-06T12:00:00Z",
            regNo = "KL02BT5555",
            route = sampleHomeUiModel.route.copy(
                name = "04:30 PM Kollam to Karunagappally"
            )
        )
    )
)

