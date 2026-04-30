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
     * Fetch Assignment Operator
     * **Fetches a list of service assignments.**                 - Requires a valid access token for authentication.
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
     * @param operatorId  (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, UPDATED_ON: updated_on (optional)
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
        @Query("operator_id") operatorId: kotlin.Int? = null,
        @Query("order_by") orderBy: AppApiServiceAssignmentOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<kotlin.collections.List<ServiceAssignmentSchema>>

}
