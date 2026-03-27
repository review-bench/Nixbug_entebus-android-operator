package com.nixbug.entebus.data.interceptor

import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.local.datastore.user.UserLocalDataSource
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.remote.operator.api.TokenApi
import com.nixbug.entebus.data.remote.operator.model.GrantType
import com.nixbug.entebus.di.NoAuthApi
import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.domain.usecase.token.SaveTokenUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Provider


class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val saveTokenUseCase: Provider<SaveTokenUseCase>,
    @NoAuthApi private val tokenApiNoAuth: Provider<TokenApi>
) : Interceptor {

    @Volatile
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path == "/operator/company/account/token"
        ) {
            return chain.proceed(request)
        }

        val now = System.currentTimeMillis()
        val expiryTime = tokenManager.expiryTime
        val refreshToken = tokenManager.refreshToken
        val refreshBefore = tokenManager.refreshBefore

        val shouldRefresh =
            refreshBefore != null && now < refreshBefore && expiryTime != null && now >= expiryTime - (59 * 60 * 1000L)

        if (shouldRefresh && !refreshToken.isNullOrEmpty()) {
            refreshTokenSafe(refreshToken)
        }

        val currentAuthToken = tokenManager.accessToken

        val requestWithToken = if (!currentAuthToken.isNullOrEmpty()) {
            request.newBuilder()
                .header("Authorization", "Bearer $currentAuthToken")
                .build()
        } else request

        val response = chain.proceed(requestWithToken)

        val responseBody = response.body
        val content = responseBody?.string()

        val newResponse = response.newBuilder()
            .body(content?.toResponseBody(responseBody.contentType()))
            .build()

        if (response.code == 401) {
            response.close()
            runBlocking {
                tokenLocalDataSource.clearToken()
                userLocalDataSource.clearUser()
            }
        }

        return newResponse
    }

    @Synchronized
    private fun refreshTokenSafe(refreshToken: String): String? {
        if (isRefreshing) return tokenManager.accessToken

        isRefreshing = true

        val newToken: String? = try {
            runBlocking {
                val response = tokenApiNoAuth.get().refreshTokenCompanyAccountTokenRefreshPost(
                    refreshToken = refreshToken,
                    grantType = GrantType.refresh_token
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.accessToken.isNotEmpty()) {
                        val tokenToSave = CreateTokenModel(
                            operatorId = body.operatorId,
                            expiresIn = body.expiresIn,
                            refreshBefore = body.refreshBefore,
                            createdOn = body.createdOn,
                            accessToken = body.accessToken,
                            refreshToken = body.refreshToken,
                        )
                        saveTokenUseCase.get()(tokenToSave)
                        body.accessToken
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        } finally {
            isRefreshing = false
        }

        return newToken
    }
}
