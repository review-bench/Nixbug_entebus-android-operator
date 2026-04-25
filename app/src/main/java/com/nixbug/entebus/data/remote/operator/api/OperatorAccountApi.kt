package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.account.OperatorSchema
import com.nixbug.entebus.data.remote.operator.model.account.OperatorType
import com.nixbug.entebus.data.remote.operator.model.account.AccountStatus
import com.nixbug.entebus.data.remote.operator.model.account.AppApiOperatorAccountOrderBy
import com.nixbug.entebus.data.remote.operator.model.account.GenderType
import com.nixbug.entebus.data.remote.operator.model.account.OrderIn
import okhttp3.RequestBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface OperatorAccountApi {

    /**
     * GET company/account
     * Fetch Account Operator
     * **Fetches a list of operators.**                 - Requires a valid access token for authentication.                 - Only operators belonging to the same company as the logged-in operator will be returned.                 - Common search supports searching by id, username, full_name, description, phone_number, and email_id.
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
     * @param updatedOnGe  (optional)
     * @param updatedOnLe  (optional)
     * @param username  (optional)
     * @param gender OTHER: 1, FEMALE: 2, MALE: 3, TRANSGENDER: 4 (optional)
     * @param fullName  (optional)
     * @param emailId  (optional)
     * @param phoneNumber  (optional)
     * @param search  (optional)
     * @param description  (optional)
     * @param typeList NORMAL: 1, OWNER: 2, MANAGER: 3, HR: 4, LEGAL: 5, ADMIN: 6, BOT: 7 (optional)
     * @param statusList ACTIVE: 1, SUSPENDED: 2 (optional)
     * @param orderBy ID: id, CREATED_ON: created_on, UPDATED_ON: updated_on (optional)
     * @param orderIn ASCENDING: asc, DESCENDING: desc (optional)
     * @return [kotlin.collections.List<OperatorSchema>]
     */
    @GET("company/account")
    suspend fun getOperatorAccounts(
        @Query("offset") offset: Int? = 0,
        @Query("limit") limit: Int? = 20,
        @Query("id") id: Int? = null,
        @Query("id_ge") idGe: Int? = null,
        @Query("id_le") idLe: Int? = null,
        @Query("id_list") idList: @JvmSuppressWildcards List<Int>? = null,
        @Query("created_on_ge") createdOnGe: String? = null,
        @Query("created_on_le") createdOnLe: String? = null,
        @Query("updated_on_ge") updatedOnGe: String? = null,
        @Query("updated_on_le") updatedOnLe: String? = null,
        @Query("username") username: String? = null,
        @Query("gender") gender: GenderType? = null,
        @Query("full_name") fullName: String? = null,
        @Query("email_id") emailId: String? = null,
        @Query("phone_number") phoneNumber: String? = null,
        @Query("search") search: String? = null,
        @Query("description") description: String? = null,
        @Query("type_list") typeList: @JvmSuppressWildcards List<OperatorType>? = null,
        @Query("status_list") statusList: @JvmSuppressWildcards List<AccountStatus>? = null,
        @Query("order_by") orderBy: AppApiOperatorAccountOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<List<OperatorSchema>>

    /**
     * PATCH company/account/{id}
     * Update Account Operator
     * **Updates an existing operator account.**                 - Requires a valid access token.                 - Logged-in operator must have &#x60;company.operator.update&#x60; permission to update other operators.                 - Operator can update their own account except status.                 - Empty PATCH requests are allowed and will result in no changes.
     * Responses:
     *  - 200: Successful Response
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *  - 422: Validation Error
     *
     * @param id
     * @return [OperatorSchema]
     */
    @PATCH("company/account/{id}")
    suspend fun updateOperatorAccount(
        @Path("id") id: Int,
        @Body body: RequestBody
    ): Response<OperatorSchema>

}
