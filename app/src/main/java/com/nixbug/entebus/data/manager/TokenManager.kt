package com.nixbug.entebus.data.manager

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    @Volatile
    var accessToken: String? = null
    var refreshToken: String? = null
    var operatorId: Int? = null
    var expiryTime: Long? = null
    var refreshBefore: Long? = null
    var createdOn: Long? = null

    fun clearToken(){
        accessToken = null
        refreshToken = null
        operatorId = null
        expiryTime = null
        refreshBefore = null
        createdOn = null
    }

}
