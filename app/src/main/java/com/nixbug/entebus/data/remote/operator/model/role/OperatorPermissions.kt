package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Operator related permissions.
 *
 * @param create Allow creation
 * @param update Allow updating
 * @param delete Allow deletion
 * @param role
 * @param token
 */


data class OperatorPermissions (

    /* Allow creation */
    @SerializedName("create")
    val create: Boolean,

    /* Allow updating */
    @SerializedName("update")
    val update: Boolean,

    /* Allow deletion */
    @SerializedName("delete")
    val delete: Boolean,

    @SerializedName("role")
    val role: CRUDPermission,

    @SerializedName("token")
    val token: TokenPermission

)
