package com.nixbug.entebus.data.interceptor

import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.remote.operator.api.OperatorTokenApi
import com.nixbug.entebus.data.remote.operator.model.token.GrantType
import com.nixbug.entebus.di.NoAuthApi
import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.domain.repository.SessionRepository
import com.nixbug.entebus.utils.network.RefreshResult
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider


class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val sessionRepository: SessionRepository,
    @NoAuthApi private val tokenApiNoAuth: Provider<OperatorTokenApi>
) : Interceptor {

    private val refreshTokenMutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path == "/operator/company/account/token") {
            return chain.proceed(request)
        }
        if (path == "/operator/company/account/token/revoke") {
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
            when (val result = refreshTokenSafe(refreshToken)) {
                is RefreshResult.Success -> {}

                is RefreshResult.InvalidRefreshToken -> {
                    sessionRepository.notifySessionExpired()
                }

                else -> {}
            }
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
                when (val result = refreshTokenSafe(refreshToken, forceRefresh = true)) {

                    is RefreshResult.Success -> {
                        response.close()

                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer ${result.token}")
                            .header("X-Retry", "true")
                            .build()

                        return chain.proceed(newRequest)
                    }

                    is RefreshResult.InvalidRefreshToken -> {
                        sessionRepository.notifySessionExpired()
                    }

                    is RefreshResult.NetworkError,
                    is RefreshResult.UnknownError -> {
                        return response
                    }
                }
            } else {
                sessionRepository.notifySessionExpired()
            }
        }

        return response
    }

    private fun refreshTokenSafe(
        refreshToken: String,
        forceRefresh: Boolean = false
    ): RefreshResult =
        runBlocking {
            refreshTokenMutex.withLock {
                val currentToken = tokenManager.accessToken
                val now = System.currentTimeMillis()
                val expiryTime = tokenManager.expiryTime

                if (!forceRefresh) {
                    val isStillStale = expiryTime == null || now >= (expiryTime - 5 * 60 * 1000L)
                    if (!isStillStale && !currentToken.isNullOrEmpty()) return@runBlocking RefreshResult.Success(
                        currentToken
                    )
                }
                try {
                    val response = tokenApiNoAuth.get()
                        .refreshTokenCompanyAccountTokenRefreshPost(
                            refreshToken = refreshToken,
                            grantType = GrantType.refresh_token
                        )

                    when {
                        response.isSuccessful -> {
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

                                RefreshResult.Success(body.accessToken)
                            } else {
                                RefreshResult.UnknownError
                            }
                        }

                        response.code() == 400 || response.code() == 401 -> {
                            RefreshResult.InvalidRefreshToken
                        }

                        else -> {
                            RefreshResult.UnknownError
                        }
                    }

                } catch (e: IOException) {
                    RefreshResult.NetworkError
                } catch (e: Exception) {
                    RefreshResult.UnknownError
                }
            }
        }
}
