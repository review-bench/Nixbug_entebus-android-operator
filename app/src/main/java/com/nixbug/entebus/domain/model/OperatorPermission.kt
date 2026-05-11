package com.nixbug.entebus.domain.model

data class OperatorPermission(
    val canCreateTicket: Boolean = false,
    val canUpdateService: Boolean = false,
)
