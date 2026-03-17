package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.PublicCompanyModel
import com.nixbug.entebus.utils.network.Resource

interface PublicCompanyRepository {
    suspend fun fetchPublicCompany(
        location: String?,
        name: String?,
        offset: Int,
        limit: Int
    ): Resource<List<PublicCompanyModel>>
}
