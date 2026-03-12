package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.domain.model.CreateTokenModel
import org.openapitools.client.models.OperatorTokenSchema

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
