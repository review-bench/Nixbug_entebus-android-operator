package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Single action create permission.
 *
 * @param create Allow creation
 */


data class CreatePermission (

    /* Allow creation */
    @SerializedName("create")
    val create: Boolean

)
