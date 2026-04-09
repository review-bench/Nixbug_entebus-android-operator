package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.accountimage.OperatorImageSchema
import com.nixbug.entebus.domain.model.OperatorAccountImageModel

fun OperatorImageSchema.toOperatorAccountImageDomain(): OperatorAccountImageModel {
    return OperatorAccountImageModel(
        id = id
    )
}
