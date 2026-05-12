package com.nixbug.entebus.data.remote.operator.model.rolemap

import com.google.gson.annotations.SerializedName

/**
 * Schema for operator role mapping response.
 *
 * @param id
 * @param companyId
 * @param roleId
 * @param operatorId
 * @param createdOn
 * @param updatedOn
 */


data class OperatorRoleMapSchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("role_id")
    val roleId: Int,

    @SerializedName("operator_id")
    val operatorId: Int,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("updated_on")
    val updatedOn: String?

)
