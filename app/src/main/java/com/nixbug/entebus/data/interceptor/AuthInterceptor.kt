package com.nixbug.entebus.data.interceptor

import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.remote.operator.api.TokenApi
import com.nixbug.entebus.data.remote.operator.model.GrantType
import com.nixbug.entebus.di.NoAuthApi
import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.domain.repository.SessionRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider


class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val sessionRepository: SessionRepository,
    @NoAuthApi private val tokenApiNoAuth: Provider<TokenApi>
) : Interceptor {

    private val refreshTokenMutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path == "/operator/company/account/token") {
            return chain.proceed(request)
        }
        if (path == "/operator/company/account/token/refresh") {
            val accessToken = tokenManager.accessToken
            if (!accessToken.isNullOrEmpty()) {
                val req = request.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
                return chain.proceed(req)
            }
        }

        val now = System.currentTimeMillis()
        val expiryTime = tokenManager.expiryTime
        val refreshToken = tokenManager.refreshToken
        val refreshBefore = tokenManager.refreshBefore

        val isAccessExpiring =
            expiryTime != null &&
                    now >= (expiryTime - 5 * 60 * 1000L)

        val isRefreshTokenValid =
            refreshBefore != null &&
                    now < refreshBefore

        val shouldRefresh =
            isAccessExpiring && isRefreshTokenValid

        if (shouldRefresh && !refreshToken.isNullOrEmpty()) {
            refreshTokenSafe(refreshToken)
        }

        val currentAuthToken = tokenManager.accessToken

        val requestWithToken =
            if (!currentAuthToken.isNullOrEmpty()) {
                request.newBuilder()
                    .header("Authorization", "Bearer $currentAuthToken")
                    .build()
            } else request

        val response = chain.proceed(requestWithToken)

        if (response.code == 401 && request.header("X-Retry") != "true") {
            val refreshToken = tokenManager.refreshToken
            if (!refreshToken.isNullOrEmpty()) {
                val newToken = refreshTokenSafe(refreshToken, forceRefresh = true)
                if (!newToken.isNullOrEmpty() && newToken != currentAuthToken) {
                    response.close()
                    val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .header("X-Retry", "true")
                        .build()
                    return chain.proceed(request = newRequest)
                }
            }
            sessionRepository.notifySessionExpired()
        }

        return response
    }


    private fun refreshTokenSafe(refreshToken: String, forceRefresh: Boolean = false): String? =
        runBlocking {
            refreshTokenMutex.withLock {
                val currentToken = tokenManager.accessToken
                val now = System.currentTimeMillis()
                val expiryTime = tokenManager.expiryTime

                if (!forceRefresh) {
                    val isStillStale = expiryTime == null || now >= (expiryTime - 5 * 60 * 1000L)
                    if (!isStillStale) return@runBlocking currentToken
                }
                try {

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
                            tokenLocalDataSource.saveToken(tokenToSave)
                            body.accessToken
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
}
