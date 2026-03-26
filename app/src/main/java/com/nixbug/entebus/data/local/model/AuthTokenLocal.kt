package com.nixbug.entebus.data.local.model

data class AuthTokenLocal(
    val operatorId: Int?,
    val refreshBefore: Long?,
    val createdOn: Long?,
    val expiryTime: Long?,
    val accessToken: String?,
    val refreshToken: String?
)
