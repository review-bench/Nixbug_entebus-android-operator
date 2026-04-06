package com.nixbug.entebus.data.remote.operator.model.token

import com.google.gson.annotations.SerializedName

/**
 * Authentication grant types.
 *
 * Values: password,refresh_token
 */

enum class GrantType(val value: String) {

    @SerializedName(value = "password")
    password("password"),

    @SerializedName(value = "refresh_token")
    refresh_token("refresh_token");

    /**
     * Override [toString()] to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): String = value

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: Any?): String? = if (data is GrantType) "$data" else null

        /**
         * Returns a valid [GrantType] for [data], null otherwise.
         */
        fun decode(data: Any?): GrantType? = data?.let {
            val normalizedData = "$it".lowercase()
            values().firstOrNull { value ->
                it == value || normalizedData == "$value".lowercase()
            }
        }
    }
}
