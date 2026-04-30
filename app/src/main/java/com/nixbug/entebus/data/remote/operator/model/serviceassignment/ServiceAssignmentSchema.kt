package com.nixbug.entebus.data.remote.operator.model.serviceassignment

import com.google.gson.annotations.SerializedName

/**
 * Schema for service assignment response.
 *
 * @param id
 * @param companyId
 * @param serviceId
 * @param operatorId
 * @param createdOn
 * @param updatedOn
 */


data class ServiceAssignmentSchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("service_id")
    val serviceId: Int,

    @SerializedName("operator_id")
    val operatorId: Int,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("updated_on")
    val updatedOn: String?

)
