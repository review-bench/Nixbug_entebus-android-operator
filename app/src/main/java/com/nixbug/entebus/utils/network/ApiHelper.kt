package com.nixbug.entebus.utils.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import java.io.IOException

suspend fun <T, R> apiHelper(
    apiCall: suspend () -> Response<T>,
    mapper: (T) -> R
): Resource<R> {

    return try {

        val response = apiCall()

        if (response.isSuccessful) {

            val body = response.body()

            println("Response Body: $body")

            if (body != null) {
                Resource.Success(mapper(body))

            } else {
                Resource.Error(
                    message = "Empty response from server",
                )
            }

        } else {

            val errorBody = response.errorBody()?.string()
            val responseCode = response.code()
            var errorMessage: String? = ""

            if (responseCode == 422) {

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
                errorMessage = try {
                    val jsonObject = Gson().fromJson(errorBody, JsonObject::class.java)
                    jsonObject.get("detail")?.asString
                } catch (e: Exception) {
                    errorBody
                }
            }

            Resource.Error(
                message = errorMessage,
                statusCode = response.code()
            )
        }

    } catch (e: IOException) {

        Resource.Error(
            message = "Network error",
            statusCode = null
        )

    } catch (e: Exception) {

        Resource.Error(
            message = e.localizedMessage ?: "Unexpected error",
            statusCode = null
        )
    }
}
