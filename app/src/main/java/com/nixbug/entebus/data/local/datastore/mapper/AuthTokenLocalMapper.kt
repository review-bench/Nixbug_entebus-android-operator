package com.nixbug.entebus.data.local.datastore.mapper

import com.nixbug.entebus.data.local.datastore.model.AuthTokenLocal
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
