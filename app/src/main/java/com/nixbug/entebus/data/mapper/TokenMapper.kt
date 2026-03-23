package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.OperatorTokenSchema
import com.nixbug.entebus.domain.model.CreateTokenModel

fun OperatorTokenSchema.toTokenDomain(): CreateTokenModel {
    return CreateTokenModel(
        operatorId = operatorId,
        expiresIn = expiresIn,
        refreshBefore = refreshBefore,
        createdOn = createdOn,
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}
