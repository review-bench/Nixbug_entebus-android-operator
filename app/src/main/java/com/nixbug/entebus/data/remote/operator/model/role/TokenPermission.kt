package com.nixbug.entebus.data.remote.operator.model.role

import com.google.gson.annotations.SerializedName

/**
 * Specialized permissions for token management.
 *
 * @param fetch Allow fetching token details
 * @param delete Allow deleting token
 */


data class TokenPermission(

    /* Allow fetching token details */
    @SerializedName("fetch")
    val fetch: Boolean,

    /* Allow deleting token */
    @SerializedName("delete")
    val delete: Boolean

)
