package com.nixbug.entebus.data.remote.operator.model.account

import com.google.gson.annotations.SerializedName

/**
 * Form data for updating an operator account.
 *
 * @param password
 * @param gender OTHER: 1, FEMALE: 2, MALE: 3, TRANSGENDER: 4
 * @param description
 * @param type NORMAL: 1, OWNER: 2, MANAGER: 3, HR: 4, LEGAL: 5, ADMIN: 6, BOT: 7
 * @param fullName
 * @param status ACTIVE: 1, SUSPENDED: 2
 * @param phoneNumber
 * @param emailId
 */


data class AppApiOperatorAccountUpdateForm(

    @SerializedName("password")
    val password: String? = null,

    /* OTHER: 1, FEMALE: 2, MALE: 3, TRANSGENDER: 4 */
    @SerializedName("gender")
    val gender: GenderType? = null,

    @SerializedName("description")
    val description: String? = null,

    /* NORMAL: 1, OWNER: 2, MANAGER: 3, HR: 4, LEGAL: 5, ADMIN: 6, BOT: 7 */
    @SerializedName("type")
    val type: OperatorType? = null,

    @SerializedName("full_name")
    val fullName: String? = null,

    /* ACTIVE: 1, SUSPENDED: 2 */
    @SerializedName("status")
    val status: AccountStatus? = null,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("email_id")
    val emailId: String? = null

) {


}
