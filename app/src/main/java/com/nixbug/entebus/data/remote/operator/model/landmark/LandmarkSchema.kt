package com.nixbug.entebus.data.remote.operator.model.landmark

import com.google.gson.annotations.SerializedName

/**
 * Schema for landmark response.
 *
 * @param id
 * @param name
 * @param version
 * @param aliasNames
 * @param boundary
 * @param type
 * @param updatedOn
 * @param createdOn
 */


data class LandmarkSchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("version")
    val version: Int,

    @SerializedName("alias_names")
    val aliasNames: List<String>?,

    @SerializedName("boundary")
    val boundary: String,

    @SerializedName("type")
    val type: Int,

    @SerializedName("updated_on")
    val updatedOn: String?,

    @SerializedName("created_on")
    val createdOn: String

)
