package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Schema for operator role response.
 *
 * @param id
 * @param companyId
 * @param name
 * @param permissions
 * @param createdOn
 * @param updatedOn
 */


data class OperatorRoleSchema (

    @SerializedName("id")
    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("permissions")
    val permissions: PermissionSchemaOutput,

    @SerializedName("created_on")
    val createdOn: String,

    @SerializedName("updated_on")
    val updatedOn: String?

)
