package com.nixbug.entebus.data.remote.operator.model.service

import com.google.gson.annotations.SerializedName

/**
 * Lifecycle states of a service.
 *
 * Values: _1,_2,_3,_4,_5
 */

enum class ServiceStatus(val value: Int) {

    @SerializedName(value = "1")
    _1(1),

    @SerializedName(value = "2")
    _2(2),

    @SerializedName(value = "3")
    _3(3),

    @SerializedName(value = "4")
    _4(4),

    @SerializedName(value = "5")
    _5(5);

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
        fun encode(data: Any?): String? = if (data is ServiceStatus) "$data" else null

        /**
         * Returns a valid [ServiceStatus] for [data], null otherwise.
         */
        fun decode(data: Any?): ServiceStatus? = data?.let {
            val normalizedData = "$it".lowercase()
            values().firstOrNull { value ->
                it == value || normalizedData == "$value".lowercase()
            }
        }
    }
}
