package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.OperatorTokenSchema
import com.nixbug.entebus.domain.model.CreateTokenModel

fun OperatorTokenSchema.toTokenDomain(): CreateTokenModel {
    return CreateTokenModel(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresIn = expiresIn,
        operatorId = operatorId,
        refreshBefore = refreshBefore,
        createdOn = createdOn
    )
}
