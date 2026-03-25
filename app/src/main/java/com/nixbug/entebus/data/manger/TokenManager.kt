package com.nixbug.entebus.data.manger

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    @Volatile
    var accessToken: String? = null
}
