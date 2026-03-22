package com.nixbug.entebus.domain.model

data class CreateTokenModel(
    val operatorId: Int,
    val expiresIn: Int,
    val refreshBefore: String,
    val createdOn: String,
    val accessToken: String,
    val refreshToken: String,
)
