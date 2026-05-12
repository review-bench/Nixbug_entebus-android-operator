package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Generic CRUD permission set — reused by most entities.
 *
 * @param create Allow creation
 * @param update Allow updating
 * @param delete Allow deletion
 */


data class CRUDPermission(

    /* Allow creation */
    @SerializedName("create")
    val create: Boolean,

    /* Allow updating */
    @SerializedName("update")
    val update: Boolean,

    /* Allow deletion */
    @SerializedName("delete")
    val delete: Boolean

)
