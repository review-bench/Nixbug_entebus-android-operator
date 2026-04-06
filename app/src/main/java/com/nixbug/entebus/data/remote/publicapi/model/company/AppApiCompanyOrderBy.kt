package com.nixbug.entebus.data.remote.publicapi.model.company

import com.google.gson.annotations.SerializedName

/**
 * Enum for ordering company results.
 *
 * Values: id,created_on,updated_on,location
 */

enum class AppApiCompanyOrderBy(val value: String) {

    @SerializedName(value = "id")
    id("id"),

    @SerializedName(value = "created_on")
    created_on("created_on"),

    @SerializedName(value = "updated_on")
    updated_on("updated_on"),

    @SerializedName(value = "location")
    location("location");

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
        fun encode(data: Any?): String? = if (data is AppApiCompanyOrderBy) "$data" else null

        /**
         * Returns a valid [AppApiCompanyOrderBy] for [data], null otherwise.
         */
        fun decode(data: Any?): AppApiCompanyOrderBy? = data?.let {
            val normalizedData = "$it".lowercase()
            values().firstOrNull { value ->
                it == value || normalizedData == "$value".lowercase()
            }
        }
    }
}
