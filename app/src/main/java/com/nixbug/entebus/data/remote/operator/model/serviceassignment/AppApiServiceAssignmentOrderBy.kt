package com.nixbug.entebus.data.remote.operator.model.serviceassignment

import com.google.gson.annotations.SerializedName

/**
 * Enum for ordering results.
 *
 * Values: id,created_on,updated_on,starting_at
 */

enum class AppApiServiceAssignmentOrderBy(val value: kotlin.String) {

    @SerializedName(value = "id")
    id("id"),

    @SerializedName(value = "created_on")
    created_on("created_on"),

    @SerializedName(value = "updated_on")
    updated_on("updated_on"),

    @SerializedName(value = "starting_at")
    starting_at("starting_at");

    /**
     * Override [toString()] to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): kotlin.String = value

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: kotlin.Any?): kotlin.String? =
            if (data is AppApiServiceAssignmentOrderBy) "$data" else null

        /**
         * Returns a valid [AppApiServiceAssignmentOrderBy] for [data], null otherwise.
         */
        fun decode(data: kotlin.Any?): AppApiServiceAssignmentOrderBy? = data?.let {
            val normalizedData = "$it".lowercase()
            values().firstOrNull { value ->
                it == value || normalizedData == "$value".lowercase()
            }
        }
    }
}
