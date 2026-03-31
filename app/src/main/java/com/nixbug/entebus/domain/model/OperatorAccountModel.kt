package com.nixbug.entebus.domain.model

data class OperatorAccountModel(
    val id: Int,
    val username: String,
    val gender: Int,
    val fullName: String?,
    val status: Int,
    val phoneNumber: String?,
    val emailId: String?,
    val createdOn: String,
)
