package com.nixbug.entebus.data.manager

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    @Volatile
    var accessToken: String? = null
}
