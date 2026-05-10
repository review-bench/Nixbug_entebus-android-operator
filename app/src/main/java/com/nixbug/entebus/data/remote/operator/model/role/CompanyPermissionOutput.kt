package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Company related permissions.
 *
 * @param update Allow updating company details
 * @param vehicle
 * @param fare
 * @param route
 * @param `operator`
 * @param service
 * @param schedule
 */


data class CompanyPermissionOutput (

    /* Allow updating company details */
    @SerializedName("update")
    val update: Boolean,

    @SerializedName("vehicle")
    val vehicle: CRUDPermission,

    @SerializedName("fare")
    val fare: CRUDPermission,

    @SerializedName("route")
    val route: CRUDPermission,

    @SerializedName("operator")
    val `operator`: OperatorPermissions,

    @SerializedName("service")
    val service: ServicePermissions,

    @SerializedName("schedule")
    val schedule: CRUDPermission

) {


}