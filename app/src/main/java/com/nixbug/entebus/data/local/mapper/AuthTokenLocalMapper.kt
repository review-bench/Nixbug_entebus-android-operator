package com.nixbug.entebus.data.local.mapper

import com.nixbug.entebus.data.local.model.AuthTokenLocal
import com.nixbug.entebus.domain.model.local.AuthToken

fun AuthTokenLocal.toDomain(): AuthToken {
    return AuthToken(
        operatorId = operatorId,
        expiryTime = expiryTime,
        refreshBefore = refreshBefore,
        createdOn = createdOn,
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}