package com.nixbug.entebus.data.remote.publicapi.model.company

import com.google.gson.annotations.SerializedName

/**
 * Classification of companies.
 *
 * Values: _1,_2,_3
 */

enum class
CompanyType(val value: Int) {

    @SerializedName(value = "1")
    _1(1),

    @SerializedName(value = "2")
    _2(2),

    @SerializedName(value = "3")
    _3(3);

    /**
     * Override [toString()] to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): String = value.toString()

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: Any?): String? = if (data is CompanyType) "$data" else null

        /**
         * Returns a valid [CompanyType] for [data], null otherwise.
         */
        fun decode(data: Any?): CompanyType? = data?.let {
            val normalizedData = "$it".lowercase()
            values().firstOrNull { value ->
                it == value || normalizedData == "$value".lowercase()
            }
        }
    }
}
