package com.nixbug.entebus.data.manager

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    @Volatile
    var accessToken: String? = null

    @Volatile
    var refreshToken: String? = null

    @Volatile
    var expiryTime: Long? = null

    @Volatile
    var refreshBefore: Long? = null

    @Volatile
    var createdOn: Long? = null

    @Volatile
    var operatorId: Int? = null

    fun clearToken() {
        accessToken = null
        refreshToken = null
        operatorId = null
        expiryTime = null
        refreshBefore = null
        createdOn = null
    }

}
