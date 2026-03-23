package com.nixbug.entebus.data.local.datastore.token

data class TokenLocalModel(
    val operatorId: Int?,
    val refreshBefore: String?,
    val createdOn: String?,
    val expiryTime: Long?,
    val accessToken: String?,
    val refreshToken: String?
)
