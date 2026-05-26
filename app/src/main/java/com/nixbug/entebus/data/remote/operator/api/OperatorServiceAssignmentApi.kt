package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.serviceassignment.AppApiServiceAssignmentOrderBy
import com.nixbug.entebus.data.remote.operator.model.serviceassignment.OrderIn
import com.nixbug.entebus.data.remote.operator.model.serviceassignment.ServiceAssignmentSchema
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface OperatorServiceAssignmentApi {

    /**
     * GET company/service/assignment
     * Fetch service assignment
     * **Fetches a list of service assignments.**
     * - Requires a valid access token for authentication.
     * - Operators can filter service assignments by service, operator, and service starting time.
     * Responses:
     *  - 200: Successful Response
     *  - 401: Unauthorized
     *  - 422: Validation Error
     *
     * @param updatedOnGe  (optional)
     * @param updatedOnLe  (optional)
     * @param createdOnGe  (optional)
     * @param createdOnLe  (optional)
     * @param id  (optional)
     * @param idGe  (optional)
     * @param idLe  (optional)
     * @param idList  (optional)
     * @param offset  (optional, default to 0)
     * @param limit  (optional, default to 20)
     * @param serviceId  (optional)
     * @param serviceIdExcluding  (optional)
     * @param operatorId  (optional)
     * @param startingAtGe  (optional)
     * @param startingAtLe  (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, UPDATED_ON: updated_on, STARTING_AT: starting_at (optional)
     * @param orderIn ASCENDING: asc, DESCENDING: desc (optional)
     * @return [kotlin.collections.List<ServiceAssignmentSchema>]
     */
    @GET("company/service/assignment")
    suspend fun getAssignedServices(
        @Query("updated_on_ge") updatedOnGe: kotlin.String? = null,
        @Query("updated_on_le") updatedOnLe: kotlin.String? = null,
        @Query("created_on_ge") createdOnGe: kotlin.String? = null,
        @Query("created_on_le") createdOnLe: kotlin.String? = null,
        @Query("id") id: kotlin.Int? = null,
        @Query("id_ge") idGe: kotlin.Int? = null,
        @Query("id_le") idLe: kotlin.Int? = null,
        @Query("id_list") idList: @JvmSuppressWildcards kotlin.collections.List<kotlin.Int>? = null,
        @Query("offset") offset: kotlin.Int? = 0,
        @Query("limit") limit: kotlin.Int? = 20,
        @Query("service_id") serviceId: kotlin.Int? = null,
        @Query("service_id_excluding") serviceIdExcluding: @JvmSuppressWildcards kotlin.collections.List<kotlin.Int>? = null,
        @Query("operator_id") operatorId: kotlin.Int? = null,
        @Query("starting_at_ge") startingAtGe: kotlin.String? = null,
        @Query("starting_at_le") startingAtLe: kotlin.String? = null,
        @Query("order_by") orderBy: AppApiServiceAssignmentOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<kotlin.collections.List<ServiceAssignmentSchema>>

}
