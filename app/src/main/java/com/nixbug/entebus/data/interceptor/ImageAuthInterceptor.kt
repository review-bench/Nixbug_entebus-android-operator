package com.nixbug.entebus.data.interceptor

import com.nixbug.entebus.data.manager.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ImageAuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.accessToken

        val newRequest = chain.request().newBuilder()
            .apply {
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(newRequest)
    }
}
