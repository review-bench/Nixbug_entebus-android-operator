package com.nixbug.entebus.data.remote.operator.model.accountimage

import com.google.gson.annotations.SerializedName

/**
 * Schema for operator image response.
 *
 * @param id
 * @param companyId
 * @param operatorId
 * @param fileName
 * @param fileType
 * @param fileSize
 * @param createdOn
 */


data class OperatorImageSchema(

    @SerializedName("id")
    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("operator_id")
    val operatorId: Int,

    @SerializedName("file_name")
    val fileName: String,

    @SerializedName("file_type")
    val fileType: String,

    @SerializedName("file_size")
    val fileSize: Int,

    @SerializedName("created_on")
    val createdOn: String

)
