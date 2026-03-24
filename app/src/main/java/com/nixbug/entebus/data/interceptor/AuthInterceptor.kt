package com.nixbug.entebus.data.interceptor

import okhttp3.Interceptor
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()

        val path = request.url.encodedPath

        if (
            path == "/operator/company/account/token" ||
            path == "/operator/company/account/token/refresh"
        ) {
            return chain.proceed(request)
        }

        val token = tokenManager.accessToken

        val newRequest = if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}