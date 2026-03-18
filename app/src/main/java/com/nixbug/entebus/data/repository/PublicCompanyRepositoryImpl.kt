package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.mapper.toPublicCompanyDomain
import com.nixbug.entebus.data.remote.publicapi.api.CompanyApi
import com.nixbug.entebus.data.remote.publicapi.model.AppApiCompanyOrderBy
import com.nixbug.entebus.data.remote.publicapi.model.OrderIn
import com.nixbug.entebus.domain.repository.PublicCompanyRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.network.apiHelper
import com.nixbug.entebus.domain.model.PublicCompanyModel
import javax.inject.Inject

class PublicCompanyRepositoryImpl @Inject constructor(
    private val companyApi: CompanyApi
) : PublicCompanyRepository {
    override suspend fun fetchPublicCompany(
        location: String?,
        name: String?,
        offset: Int,
        limit: Int
    ): Resource<List<PublicCompanyModel>> {
        return apiHelper(
            apiCall = {
                companyApi.fetchCompanyPublicCompanyGet(
                    name = name,
                    location = location,
                    offset = offset,
                    limit = limit,
                    orderBy = location?.let { AppApiCompanyOrderBy.location },
                    orderIn = location?.let { OrderIn.asc }
                )
            },
            mapper = { response ->
                response.map { it.toPublicCompanyDomain() }
            }
        )
    }
}
