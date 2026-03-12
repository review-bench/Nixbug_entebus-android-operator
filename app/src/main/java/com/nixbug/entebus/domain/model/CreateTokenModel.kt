package com.nixbug.entebus.domain.model

data class CreateTokenModel(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val operatorId: Int,
    val refreshBefore: String,
    val createdOn: String,
)
