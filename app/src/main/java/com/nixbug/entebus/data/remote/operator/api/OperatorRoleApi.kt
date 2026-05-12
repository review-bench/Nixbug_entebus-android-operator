package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.role.AppApiOperatorRoleOrderBy
import com.nixbug.entebus.data.remote.operator.model.role.OperatorRoleSchema
import com.nixbug.entebus.data.remote.operator.model.role.OrderIn
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OperatorRoleApi {

    /**
     * GET company/role
     * Fetch Role Operator
     * **Fetches a list of operator roles.**                 - Requires a valid access token for authentication.                 - Only operator roles belonging to the same company as the logged-in operator will be returned.
     * Responses:
     *  - 200: Successful Response
     *  - 401: Unauthorized
     *  - 422: Validation Error
     *
     * @param offset  (optional, default to 0)
     * @param limit  (optional, default to 20)
     * @param id  (optional)
     * @param idGe  (optional)
     * @param idLe  (optional)
     * @param idList  (optional)
     * @param name  (optional)
     * @param createdOnGe  (optional)
     * @param createdOnLe  (optional)
     * @param updatedOnGe  (optional)
     * @param updatedOnLe  (optional)
     * @param search  (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, UPDATED_ON: updated_on (optional)
     * @param orderIn ASCENDING: asc, DESCENDING: desc (optional)
     * @return [kotlin.collections.List<OperatorRoleSchema>]
     */
    @GET("company/role")
    suspend fun fetchRoleOperatorCompanyRoleGet(
        @Query("offset") offset: kotlin.Int? = 0,
        @Query("limit") limit: kotlin.Int? = 20,
        @Query("id") id: kotlin.Int? = null,
        @Query("id_ge") idGe: kotlin.Int? = null,
        @Query("id_le") idLe: kotlin.Int? = null,
        @Query("id_list") idList: @JvmSuppressWildcards kotlin.collections.List<kotlin.Int>? = null,
        @Query("name") name: kotlin.String? = null,
        @Query("created_on_ge") createdOnGe: kotlin.String? = null,
        @Query("created_on_le") createdOnLe: kotlin.String? = null,
        @Query("updated_on_ge") updatedOnGe: kotlin.String? = null,
        @Query("updated_on_le") updatedOnLe: kotlin.String? = null,
        @Query("search") search: kotlin.String? = null,
        @Query("order_by") orderBy: AppApiOperatorRoleOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<kotlin.collections.List<OperatorRoleSchema>>

}
