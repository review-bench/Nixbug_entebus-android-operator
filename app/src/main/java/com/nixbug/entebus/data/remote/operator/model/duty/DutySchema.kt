package com.nixbug.entebus.data.remote.operator.model.duty

import com.google.gson.annotations.SerializedName

/**
 * Schema for duty response.
 *
 * @param id
 * @param companyId
 * @param operatorId
 * @param serviceId
 * @param status
 * @param startedOn
 * @param finishedOn
 * @param collection
 * @param updatedOn
 * @param createdOn
 */


data class DutySchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("operator_id")
    val operatorId: Int?,

    @SerializedName("service_id")
    val serviceId: Int,

    @SerializedName("status")
    val status: Int,

    @SerializedName("started_on")
    val startedOn: String?,

    @SerializedName("finished_on")
    val finishedOn: String?,

    @SerializedName("collection")
    val collection: String?,

    @SerializedName("updated_on")
    val updatedOn: String?,

    @SerializedName("created_on")
    val createdOn: String

)
