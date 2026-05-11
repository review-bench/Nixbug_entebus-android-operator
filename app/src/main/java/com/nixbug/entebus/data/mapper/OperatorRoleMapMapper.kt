package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.role.OperatorRoleSchema
import com.nixbug.entebus.domain.model.OperatorPermission

fun OperatorRoleSchema.toOperatorPermission(): OperatorPermission {
    return OperatorPermission(
        canCreateTicket = permissions.company.service.ticket.create,
        canUpdateService = permissions.company.service.update
    )
}
