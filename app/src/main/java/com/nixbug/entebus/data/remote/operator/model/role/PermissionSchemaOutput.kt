package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Top-level hierarchical permission structure for an OperatorRole.
 *
 * @param company
 */


data class PermissionSchemaOutput (

    @SerializedName("company")
    val company: CompanyPermissionOutput

)
