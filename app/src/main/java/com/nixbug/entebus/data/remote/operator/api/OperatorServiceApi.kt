package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.service.PrivateServiceSchema
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceOrderBy
import com.nixbug.entebus.data.remote.operator.model.service.ServiceSchema
import com.nixbug.entebus.data.remote.operator.model.service.ServiceStatus
import com.nixbug.entebus.data.remote.operator.model.service.TicketingMode
import com.nixbug.entebus.data.remote.operator.model.service.OrderIn
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OperatorServiceApi {
    /**
     * GET company/service
     * Fetch Service Operator
     * **Fetches a list of services.**                 - Requires a valid access token for authentication.
     * Responses:
     *  - 200: Successful Response
     *  - 401: Unauthorized
     *  - 422: Validation Error
     *
     * @param offset  (optional, default to 0)
     * @param limit  (optional, default to 20)
     * @param updatedOnGe  (optional)
     * @param updatedOnLe  (optional)
     * @param createdOnGe  (optional)
     * @param createdOnLe  (optional)
     * @param id  (optional)
     * @param idGe  (optional)
     * @param idLe  (optional)
     * @param idList  (optional)
     * @param search  (optional)
     * @param name  (optional)
     * @param registrationNumber  (optional)
     * @param ticketMode HYBRID: 1, DIGITAL: 2, CONVENTIONAL: 3 (optional)
     * @param statusList CREATED: 1, CACHED: 2, STARTED: 3, ENDED: 4, AUDITED: 5 (optional)
     * @param startingAtGe  (optional)
     * @param startingAtLe  (optional)
     * @param endingAtGe  (optional)
     * @param endingAtLe  (optional)
     * @param startingLandmarkId  (optional)
     * @param endingLandmarkId  (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, UPDATED_ON: updated_on, STARTING_AT: starting_at, ENDING_AT: ending_at (optional)
     * @param orderIn ASCENDING: asc, DESCENDING: desc (optional)
     * @param idExcluding  (optional)
     * @return [kotlin.collections.List<ServiceSchema>]
     */
    @GET("company/service")
    suspend fun getMaskedService(
        @Query("offset") offset: kotlin.Int? = 0,
        @Query("limit") limit: kotlin.Int? = 20,
        @Query("updated_on_ge") updatedOnGe: kotlin.String? = null,
        @Query("updated_on_le") updatedOnLe: kotlin.String? = null,
        @Query("created_on_ge") createdOnGe: kotlin.String? = null,
        @Query("created_on_le") createdOnLe: kotlin.String? = null,
        @Query("id") id: kotlin.Int? = null,
        @Query("id_ge") idGe: kotlin.Int? = null,
        @Query("id_le") idLe: kotlin.Int? = null,
        @Query("id_list") idList: @JvmSuppressWildcards kotlin.collections.List<kotlin.Int>? = null,
        @Query("search") search: kotlin.String? = null,
        @Query("name") name: kotlin.String? = null,
        @Query("registration_number") registrationNumber: kotlin.String? = null,
        @Query("ticket_mode") ticketMode: TicketingMode? = null,
        @Query("status_list") statusList: @JvmSuppressWildcards kotlin.collections.List<ServiceStatus>? = null,
        @Query("starting_at_ge") startingAtGe: kotlin.String? = null,
        @Query("starting_at_le") startingAtLe: kotlin.String? = null,
        @Query("ending_at_ge") endingAtGe: kotlin.String? = null,
        @Query("ending_at_le") endingAtLe: kotlin.String? = null,
        @Query("starting_landmark_id") startingLandmarkId: kotlin.Int? = null,
        @Query("ending_landmark_id") endingLandmarkId: kotlin.Int? = null,
        @Query("order_by") orderBy: AppApiServiceOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null,
        @Query("id_excluding") idExcluding: @JvmSuppressWildcards kotlin.collections.List<kotlin.Int>? = null
    ): Response<kotlin.collections.List<ServiceSchema>>

    /**
     * GET company/service/{id}
     * Fetch Service Details For Operator
     * **Fetch a service by its ID.**                 - Requires a valid access token for authentication.                 - If &#x60;marked_as_cached&#x60; query parameter is set to true, and the service status is currently CREATED, the status will be updated to CACHED.
     * Responses:
     *  - 200: Successful Response
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 422: Validation Error
     *
     * @param id
     * @param markedAsCached  (optional, default to false)
     * @return [PrivateServiceSchema]
     */
    @GET("company/service/{id}")
    suspend fun getServiceDetails(
        @Path("id") id: kotlin.Int,
        @Query("marked_as_cached") markedAsCached: kotlin.Boolean? = false
    ): Response<PrivateServiceSchema>
}
