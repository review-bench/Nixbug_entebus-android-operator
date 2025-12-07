package com.nixbug.entebus.presentation.screens.homeScreen

data class HomeUiModel(
    val id: Int,
    val company_id: Int,
    val name: String,
    val route: Route,
    val fare: Fare,
    val bus_id: Int,
    val ticket_mode: Int,
    val status: Int,
    val starting_at: String,
    val ending_at: String,
    val remark: String?,
    val started_on: String?,
    val finished_on: String?,
    val updated_on: String?,
    val created_on: String,
    val schedule_id: Int,
    val public_key: String,
    val regNo: String
)

data class Route(
    val id: Int,
    val name: String,
    val status: Int,
    val landmark: List<RouteLandmark>,
    val company_id: Int,
    val created_on: String,
    val start_time: String,
    val updated_on: String?
)

data class RouteLandmark(
    val id: Int,
    val route_id: Int,
    val company_id: Int,
    val created_on: String,
    val updated_on: String?,
    val landmark_id: Int,
    val arrival_delta: Int,
    val departure_delta: Int,
    val distance_from_start: Int
)

data class Fare(
    val id: Int,
    val name: String,
    val scope: Int,
    val version: Int,
    val function: String,
    val attributes: FareAttributes,
    val company_id: Int,
    val created_on: String,
    val updated_on: String?
)

data class FareAttributes(
    val extra: Map<String, Any>,
    val df_version: Int,
    val ticket_types: List<TicketType>,
    val currency_type: String,
    val distance_unit: String
)

data class TicketType(
    val id: Int,
    val name: String
)

enum class CardType {
    ONGOING, ASSIGNED, DOWNLOADED, OTHER
}