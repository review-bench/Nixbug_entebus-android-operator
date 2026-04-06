package com.nixbug.entebus.data.remote.publicapi.model.company

import com.google.gson.annotations.SerializedName

/**
 * Schema for company response for public users without revealing all details.
 *
 * @param id
 * @param name
 * @param type
 */


data class MaskedCompanySchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: Int

) {
}
