package com.nixbug.entebus.data.remote.publicapi.api

import com.nixbug.entebus.data.remote.publicapi.model.company.AppApiCompanyOrderBy
import com.nixbug.entebus.data.remote.publicapi.model.company.CompanyType
import com.nixbug.entebus.data.remote.publicapi.model.company.MaskedCompanySchema
import com.nixbug.entebus.data.remote.publicapi.model.company.OrderIn
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PublicCompanyApi {
    /**
     * GET company
     * Fetch Company Public
     * **Fetches a list of companies.**                 - Only verified companies are returned.             - Only id, name, type are returned.             - Common search supports searching by id, name and address.                 - If &#x60;location&#x60; is not provided while using &#x60;order_by&#x3D;location&#x60;, the API will fall back to default ordering by &#x60;id&#x60;.
     * Responses:
     *  - 200: Successful Response
     *  - 406: Not Acceptable
     *  - 422: Validation Error
     *
     * @param updatedOnGe  (optional)
     * @param updatedOnLe  (optional)
     * @param offset  (optional, default to 0)
     * @param limit  (optional, default to 20)
     * @param name  (optional)
     * @param createdOnGe  (optional)
     * @param createdOnLe  (optional)
     * @param id  (optional)
     * @param idGe  (optional)
     * @param idLe  (optional)
     * @param idList  (optional)
     * @param search  (optional)
     * @param location Accepts only SRID 4326 (WGS84), valid WKT string representing a &#x60;POINT&#x60;. Used for distance-based ordering. (optional)
     * @param typeList OTHER: 1, PRIVATE: 2, GOVERNMENT: 3 (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, UPDATED_ON: updated_on, LOCATION: location (optional)
     * @param orderIn ASCENDING: asc, DESCENDING: desc (optional)
     * @return [kotlin.collections.List<MaskedCompanySchema>]
     */
    @GET("company")
    suspend fun getPublicCompanies(
        @Query("updated_on_ge") updatedOnGe: kotlin.String? = null,
        @Query("updated_on_le") updatedOnLe: kotlin.String? = null,
        @Query("offset") offset: kotlin.Int? = 0,
        @Query("limit") limit: kotlin.Int? = 20,
        @Query("name") name: kotlin.String? = null,
        @Query("created_on_ge") createdOnGe: kotlin.String? = null,
        @Query("created_on_le") createdOnLe: kotlin.String? = null,
        @Query("id") id: kotlin.Int? = null,
        @Query("id_ge") idGe: kotlin.Int? = null,
        @Query("id_le") idLe: kotlin.Int? = null,
        @Query("id_list") idList: @JvmSuppressWildcards kotlin.collections.List<kotlin.Int>? = null,
        @Query("search") search: kotlin.String? = null,
        @Query("location") location: kotlin.String? = null,
        @Query("type_list") typeList: @JvmSuppressWildcards kotlin.collections.List<CompanyType>? = null,
        @Query("order_by") orderBy: AppApiCompanyOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<List<MaskedCompanySchema>>

}
