package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.OperatorSchema
import com.nixbug.entebus.domain.model.OperatorAccountModel


fun OperatorSchema.toOperatorAccountDomain(): OperatorAccountModel {
    return OperatorAccountModel(
        id=id,
        username = username,
        gender = gender,
        fullName = fullName,
        status = status,
        phoneNumber = phoneNumber,
        emailId = emailId,
        createdOn = createdOn,
    )
}