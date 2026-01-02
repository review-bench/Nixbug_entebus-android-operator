package com.nixbug.entebus.presentation.screens.homeScreen

import com.google.gson.annotations.SerializedName

data class HomeUiModel(
    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    val name: String,
    val route: Route,
    val fare: Fare,

    @SerializedName("bus_id")
    val busId: Int,

    @SerializedName("ticket_mode")
    val ticketMode: Int,

    val status: Int,

    @SerializedName("starting_at")
    val startingAt: String,

    @SerializedName("ending_at")
    val endingAt: String,

    val remark: String?,

    @SerializedName("started_on")
    val startedOn: String?,

    @SerializedName("finished_on")
    val finishedOn: String?,

    @SerializedName("updated_on")
    val updatedOn: String?,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("schedule_id")
    val scheduleId: Int,

    @SerializedName("public_key")
    val publicKey: String,

    val regNo: String
)

data class Route(
    val id: Int,
    val name: String,
    val status: Int,

    val landmark: List<RouteLandmark>,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("start_time")
    val startTime: String,

    @SerializedName("updated_on")
    val updatedOn: String?
)

data class RouteLandmark(
    val id: Int,

    @SerializedName("route_id")
    val routeId: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("updated_on")
    val updatedOn: String?,

    @SerializedName("landmark_id")
    val landmarkId: Int,

    @SerializedName("arrival_delta")
    val arrivalDelta: Int,

    @SerializedName("departure_delta")
    val departureDelta: Int,

    @SerializedName("distance_from_start")
    val distanceFromStart: Int
)

data class Fare(
    val id: Int,
    val name: String,
    val scope: Int,
    val version: Int,
    val function: String,

    val attributes: FareAttributes,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("updated_on")
    val updatedOn: String?
)

data class FareAttributes(
    val extra: Map<String, Any>,

    @SerializedName("df_version")
    val dfVersion: Int,

    @SerializedName("ticket_types")
    val ticketTypes: List<TicketType>,

    @SerializedName("currency_type")
    val currencyType: String,

    @SerializedName("distance_unit")
    val distanceUnit: String
)

data class TicketType(
    val id: Int,
    val name: String
)

enum class CardType {
    ONGOING, ASSIGNED, DOWNLOADED, OTHER
}

sealed class HomeUiEvent {
    data object ServiceAlreadyRunning : HomeUiEvent()
    data object NavigateToTicketScreen : HomeUiEvent()
}
