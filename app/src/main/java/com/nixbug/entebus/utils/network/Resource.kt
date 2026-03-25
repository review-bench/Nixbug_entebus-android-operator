package com.nixbug.entebus.utils.network

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val statusCode: Int? = null
) {
    class Success<T>(data: T, statusCode: Int? = null) : Resource<T>(data, statusCode = statusCode)
    class Empty<T>(statusCode: Int) : Resource<T>(statusCode = statusCode)
    class Error<T>(
        message: String?,
        statusCode: Int? = null,
        data: T? = null
    ) : Resource<T>(data, message, statusCode)
    class Loading<T> : Resource<T>()
}
