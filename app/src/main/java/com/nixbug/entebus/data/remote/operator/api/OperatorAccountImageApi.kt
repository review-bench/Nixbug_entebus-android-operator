package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.accountimage.AppApiOperatorImageOrderBy
import com.nixbug.entebus.data.remote.operator.model.accountimage.OperatorImageSchema
import com.nixbug.entebus.data.remote.operator.model.account.OrderIn
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OperatorAccountImageApi {
    /**
     * GET company/account/picture
     * Fetch Operator Image For Operator
     * **Fetches a list of operator images.**                 - Requires a valid access token for authentication.                 - Only operator images belonging to the same company as the logged-in operator will be returned.
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
     * @param createdOnGe  (optional)
     * @param createdOnLe  (optional)
     * @param fileName  (optional)
     * @param fileType  (optional)
     * @param fileSizeGe  (optional)
     * @param fileSizeLe  (optional)
     * @param operatorId  (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, FILE_SIZE: file_size (optional)
     * @param orderIn ASCENDING: asc, DESCENDING: desc (optional)
     * @return [kotlin.collections.List<OperatorImageSchema>]
     */
    @GET("company/account/picture")
    suspend fun fetchOperatorAccountImage(
        @Query("offset") offset: kotlin.Int? = 0,
        @Query("limit") limit: kotlin.Int? = 20,
        @Query("id") id: kotlin.Int? = null,
        @Query("id_ge") idGe: kotlin.Int? = null,
        @Query("id_le") idLe: kotlin.Int? = null,
        @Query("id_list") idList: @JvmSuppressWildcards kotlin.collections.List<kotlin.Int>? = null,
        @Query("created_on_ge") createdOnGe: kotlin.String? = null,
        @Query("created_on_le") createdOnLe: kotlin.String? = null,
        @Query("file_name") fileName: kotlin.String? = null,
        @Query("file_type") fileType: kotlin.String? = null,
        @Query("file_size_ge") fileSizeGe: kotlin.Int? = null,
        @Query("file_size_le") fileSizeLe: kotlin.Int? = null,
        @Query("operator_id") operatorId: kotlin.Int? = null,
        @Query("order_by") orderBy: AppApiOperatorImageOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<kotlin.collections.List<OperatorImageSchema>>

    /**
     * GET company/account/picture/{id}
     * Download Operator Image For Operator
     * **Download operator profile picture in original or resized resolution.**                 - Requires a valid access token for authentication.
     * Responses:
     *  - 200: Successful Response
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 422: Validation Error
     *
     * @param id
     * @param width  (optional)
     * @param height  (optional)
     * @return [kotlin.Any]
     */
    @GET("company/account/picture/{id}")
    suspend fun downloadOperatorImageForOperatorCompanyAccountPictureIdGet(
        @Path("id") id: kotlin.Int,
        @Query("width") width: kotlin.Int? = null,
        @Query("height") height: kotlin.Int? = null
    ): Response<ResponseBody>
}