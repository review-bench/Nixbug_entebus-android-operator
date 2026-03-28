package com.nixbug.entebus.utils.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T, R> apiHelper(
    apiCall: suspend () -> Response<T>,
    mapper: (T) -> R,
    clearSession: suspend () -> Unit
): Resource<R> {

    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Resource.Success(data = mapper(body), statusCode = response.code())
            } else {
                Resource.Empty(response.code())
            }

        } else {
            val responseCode = response.code()
            val errorBody = response.errorBody()?.string()
            var errorMessage =
                try {
                    val jsonObject = Gson().fromJson(errorBody, JsonObject::class.java)
                    jsonObject.get("detail")?.asString
                } catch (e: Exception) {
                    errorBody
                }

            if (responseCode == 401 && errorMessage?.contains(
                    "Invalid token",
                    ignoreCase = true
                ) == true
            ) {
                clearSession()
                errorMessage = "You have been signed out. Please sign in again."
            } else if (responseCode == 403 && errorMessage?.contains(
                    "Not authenticated",
                    ignoreCase = true
                ) == true
            ) {
                clearSession()
                errorMessage = "Session expired. You’ve been logged out"
            } else if (responseCode == 422) {
                errorMessage = try {
                    val jsonObject = Gson().fromJson(errorBody, JsonObject::class.java)
                    val detailArray = jsonObject.getAsJsonArray("detail")

                    detailArray.mapNotNull { element ->
                        val obj = element.asJsonObject
                        val locArray = obj.getAsJsonArray("loc")
                        val field = if (locArray != null && locArray.size() > 1) {
                            locArray[1].asString
                        } else {
                            null
                        }
                        val msg = obj.get("msg")?.asString
                        if (field != null && msg != null) {
                            "$field $msg"
                        } else {
                            msg
                        }
                    }.joinToString(", ")

                } catch (e: Exception) {
                    errorBody
                }
            } else {
                errorMessage = errorMessage ?: "Request failed (HTTP $responseCode)"
            }

            val finalMessage = errorMessage
            Resource.Error(
                message = finalMessage,
                statusCode = response.code()
            )
        }

    } catch (e: IOException) {
        val message = when {
            e.message?.contains("timeout", ignoreCase = true) == true ->
                "Request timed out. Please try again."

            e.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                "No internet connection. Please check your network."

            else ->
                "You're offline. Please check your internet connection and try again."
        }
        Resource.Error(
            message = message,
            statusCode = null
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Resource.Error(
            message = e.localizedMessage ?: "Unexpected error",
            statusCode = null
        )
    }
}
