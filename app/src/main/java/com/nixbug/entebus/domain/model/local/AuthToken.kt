package com.nixbug.entebus.domain.model.local

data class AuthToken(
    val operatorId: Int?,
    val refreshBefore: Long?,
    val createdOn: Long?,
    val expiryTime: Long?,
    val accessToken: String?,
    val refreshToken: String?
)