package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Duty related permissions.
 *
 * @param update Allow updating duties
 */


data class DutyPermission (

    /* Allow updating duties */
    @SerializedName("update")
    val update: Boolean

)
