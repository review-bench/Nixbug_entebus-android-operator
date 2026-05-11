package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.rolemap.AppApiOperatorRoleMapOrderBy
import com.nixbug.entebus.data.remote.operator.model.rolemap.OperatorRoleMapSchema
import com.nixbug.entebus.data.remote.operator.model.rolemap.OrderIn
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OperatorRoleMapApi {
    /**
     * GET company/account/role
     * Fetch Role Map Operator
     * **Fetches a list of operator role mappings.**                 - Requires a valid access token for authentication.                 - Only operator role mappings belonging to the same company as the logged-in operator will be returned.
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
     * @param roleId  (optional)
     * @param operatorId  (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, UPDATED_ON: updated_on (optional)
     * @param orderIn ASCENDING: asc, DESCENDING: desc (optional)
     * @return [kotlin.collections.List<OperatorRoleMapSchema>]
     */
    @GET("company/account/role")
    suspend fun getRoleMap(
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
        @Query("role_id") roleId: kotlin.Int? = null,
        @Query("operator_id") operatorId: kotlin.Int? = null,
        @Query("order_by") orderBy: AppApiOperatorRoleMapOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<kotlin.collections.List<OperatorRoleMapSchema>>
}
