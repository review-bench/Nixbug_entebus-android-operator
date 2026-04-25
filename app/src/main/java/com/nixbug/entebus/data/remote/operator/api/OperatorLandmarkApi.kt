package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.landmark.AppApiLandmarkOrderBy
import com.nixbug.entebus.data.remote.operator.model.landmark.LandmarkSchema
import com.nixbug.entebus.data.remote.operator.model.landmark.LandmarkType
import com.nixbug.entebus.data.remote.operator.model.landmark.OrderIn
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OperatorLandmarkApi {
    /**
     * GET landmark
     * Fetch Landmark Operator
     * **Fetches a list of landmarks.**                 - Requires a valid access token for authentication.                 - Common search supports searching by id, name and alias_names.                 - If &#x60;location&#x60; is not provided while using &#x60;order_by&#x3D;location&#x60;, the API will fall back to default ordering by &#x60;id&#x60;.
     * Responses:
     *  - 200: Successful Response
     *  - 406: Not Acceptable
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
     * @param location Accepts only SRID 4326 (WGS84) and a valid WKT string representing a &#x60;POINT&#x60;. (optional)
     * @param aliasNames  (optional)
     * @param typeList LOCAL: 1, VILLAGE: 2, DISTRICT: 3, STATE: 4, NATIONAL: 5 (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, UPDATED_ON: updated_on, LOCATION: location (optional)
     * @param orderIn ASCENDING: asc, DESCENDING: desc (optional)
     * @return [kotlin.collections.List<LandmarkSchema>]
     */
    @GET("landmark")
    suspend fun fetchLandmarkOperatorLandmarkGet(
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
        @Query("location") location: kotlin.String? = null,
        @Query("alias_names") aliasNames: kotlin.String? = null,
        @Query("type_list") typeList: @JvmSuppressWildcards kotlin.collections.List<LandmarkType>? = null,
        @Query("order_by") orderBy: AppApiLandmarkOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<kotlin.collections.List<LandmarkSchema>>
}
