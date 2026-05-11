package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Service related permissions.
 *
 * @param create Allow creation
 * @param update Allow updating
 * @param delete Allow deletion
 * @param duty
 * @param assignment
 * @param ticket
 * @param statement
 */


data class ServicePermissions (

    /* Allow creation */
    @SerializedName("create")
    val create: Boolean,

    /* Allow updating */
    @SerializedName("update")
    val update: Boolean,

    /* Allow deletion */
    @SerializedName("delete")
    val delete: Boolean,

    @SerializedName("duty")
    val duty: DutyPermission,

    @SerializedName("assignment")
    val assignment: CRUDPermission,

    @SerializedName("ticket")
    val ticket: CreatePermission,

    @SerializedName("statement")
    val statement: CreatePermission

)
