package com.nixbug.entebus.data.remote.utils

sealed class RefreshResult {
    data class Success(val token: String) : RefreshResult()
    object InvalidRefreshToken : RefreshResult()
    object NetworkError : RefreshResult()
    object UnknownError : RefreshResult()
}