package com.nixbug.entebus.utils.network

sealed class RefreshResult {
    data class Success(val token: String) : RefreshResult()
    object InvalidRefreshToken : RefreshResult()
    object NetworkError : RefreshResult()
    object UnknownError : RefreshResult()
}
