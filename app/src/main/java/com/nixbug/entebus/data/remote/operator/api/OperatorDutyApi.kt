package com.nixbug.entebus.data.remote.operator.api

import com.nixbug.entebus.data.remote.operator.model.duty.AppApiDutyOrderBy
import com.nixbug.entebus.data.remote.operator.model.duty.DutySchema
import com.nixbug.entebus.data.remote.operator.model.duty.DutyStatus
import com.nixbug.entebus.data.remote.operator.model.duty.OrderIn
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OperatorDutyApi {
    @GET("company/service/duty")
    suspend fun getDuty(
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
        @Query("status_list") statusList: @JvmSuppressWildcards kotlin.collections.List<DutyStatus>? = null,
        @Query("order_by") orderBy: AppApiDutyOrderBy? = null,
        @Query("order_in") orderIn: OrderIn? = null
    ): Response<kotlin.collections.List<DutySchema>>
}
