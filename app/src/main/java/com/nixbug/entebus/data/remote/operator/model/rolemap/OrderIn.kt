package com.nixbug.entebus.data.remote.operator.model.rolemap

import com.google.gson.annotations.SerializedName

/**
 * Enum for ordering results.
 *
 * Values: asc,desc
 */

enum class OrderIn(val value: String) {

    @SerializedName(value = "asc")
    asc("asc"),

    @SerializedName(value = "desc")
    desc("desc");

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
        fun encode(data: Any?): String? = if (data is OrderIn) "$data" else null

        /**
         * Returns a valid [OrderIn] for [data], null otherwise.
         */
        fun decode(data: Any?): OrderIn? = data?.let {
            val normalizedData = "$it".lowercase()
            values().firstOrNull { value ->
                it == value || normalizedData == "$value".lowercase()
            }
        }
    }
}
