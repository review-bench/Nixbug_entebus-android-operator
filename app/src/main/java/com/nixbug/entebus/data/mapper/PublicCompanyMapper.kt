package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.publicapi.model.MaskedCompanySchema
import com.nixbug.entebus.domain.model.PublicCompanyModel

fun MaskedCompanySchema.toPublicCompanyDomain(): PublicCompanyModel {
    return PublicCompanyModel(
        id = id,
        name = name
    )
}
